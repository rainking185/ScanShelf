package com.storagemanagement.app.scanshelf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Toast;

import com.storagemanagement.app.scanshelf.data.SharedParameters;
import com.storagemanagement.app.scanshelf.database.AbstractManager;
import com.storagemanagement.app.scanshelf.database.AbstractRecord;
import com.storagemanagement.app.scanshelf.database.DBConnection;
import com.storagemanagement.app.scanshelf.database.DBContract;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class SearchItem extends Activity implements SearchResultItemAdapter.ItemClickListener{

    private ConstraintLayout mSearchViewLayout;

    private SearchView mSearchView;

    private PopupWindow mCreateClass;

    private SearchResultItemAdapter mAdapter;
    private RecyclerView mSearchResultList;
    private List<String> mResultList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seach_item);

        mSearchViewLayout = (ConstraintLayout)findViewById(R.id.cl_search_item);

        mResultList = new ArrayList<>();
        mResultList.add("Horse");
        mResultList.add("Cow");
        mResultList.add("Camel");
        mResultList.add("Sheep");
        mResultList.add("Goat");

        mSearchResultList = (RecyclerView)findViewById(R.id.rv_search_result);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mSearchResultList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mSearchResultList.getContext(),layoutManager.getOrientation());
        mSearchResultList.addItemDecoration(dividerItemDecoration);

        mAdapter = new SearchResultItemAdapter(this,mResultList);
        mAdapter.setClickListener(this);
        mSearchResultList.setAdapter(mAdapter);

        mSearchView = (SearchView)findViewById(R.id.sv_search_item);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //TODO: search database and update search result list.
                mResultList.clear();
                mResultList.add(s);
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(View view, int position) {
        SharedPreferences sharedPreferences = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME,MODE_PRIVATE);
        //TODO: add class_uuid
        sharedPreferences.edit().putString(SharedParameters.OnDisplayParameters.CLASS_UUID,"").apply();
        startActivity(new Intent(this, ItemDetails.class));
    }

    public void toItemDetails(View view){
        SharedPreferences sharedPreferences = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME,MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SharedParameters.OnDisplayParameters.IS_FINDING_A_LOCATION,true).apply();
        startActivity(new Intent(this, ItemDetails.class));
    }

    public void toAddClass(View view){
        toCreateClass();
    }

    @SuppressLint("InflateParams")
    public void toCreateClass(){
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View createShelfWindow = null;
        if (inflater != null) {
            createShelfWindow = inflater.inflate(R.layout.popup_class_form,null);
        }
        mCreateClass = new PopupWindow(
                createShelfWindow,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCreateClass.setElevation(5);
        }
        mCreateClass.showAtLocation(mSearchViewLayout, Gravity.TOP,0,0);
        mCreateClass.setFocusable(true);
        mCreateClass.update();
    }

    public void cancel(View view){
        mCreateClass.dismiss();
    }

    public void create(View view){
        String category = ((EditText)mCreateClass.getContentView().findViewById(R.id.et_class_form_category)).getText().toString();
        String name = ((EditText)mCreateClass.getContentView().findViewById(R.id.et_class_form_item_name)).getText().toString();
        if(category.equals("")||name.equals("")){
            Toast.makeText(this,"Please complete compulsory(*) items.",Toast.LENGTH_LONG).show();
            return;
        }
        //TODO: check for duplicate in database
        boolean isDuplicate = false;
        if(isDuplicate){
            Toast.makeText(this,"The class exists in the database",Toast.LENGTH_LONG).show();
            return;
        }
        String unit = ((EditText)mCreateClass.getContentView().findViewById(R.id.et_class_form_unit)).getText().toString();
        String description = ((EditText)mCreateClass.getContentView().findViewById(R.id.et_class_form_description)).getText().toString();
        //TODO: add new class to database
        //TODO: Update recyclerlist
        Toast.makeText(this,"New class added.",Toast.LENGTH_LONG).show();
        mCreateClass.dismiss();
    }


}

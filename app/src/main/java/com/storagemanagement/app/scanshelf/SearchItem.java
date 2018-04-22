package com.storagemanagement.app.scanshelf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.storagemanagement.app.scanshelf.data.SharedParameters;
import com.storagemanagement.app.scanshelf.database.AbstractManager;
import com.storagemanagement.app.scanshelf.database.AbstractRecord;
import com.storagemanagement.app.scanshelf.database.DBConnection;
import com.storagemanagement.app.scanshelf.database.DBContract;

import org.json.JSONArray;

import java.util.List;

public class SearchItem extends AppCompatActivity implements SearchResultItemAdapter.ListItemClickListener{

    private static final int NUM_LIST_ITEMS = 100;

    private JSONArray[] mSearchResult;
    private String mSearchString;
    private SearchView mSearchView;

    private SearchResultItemAdapter mAdapter;
    private RecyclerView mSearchResultList;

    private DBConnection mDb;
    private AbstractManager mManager;
    private List<AbstractRecord> mItemClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seach_item);

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDb = DBConnection.local();
        mManager = mDb.getManager(DBContract.ITEM_CLASS);
        mItemClass = mManager.items();

        mSearchResultList = (RecyclerView)findViewById(R.id.rv_search_result);

        mSearchResultList.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new SearchResultItemAdapter(NUM_LIST_ITEMS,this);
        mSearchResultList.setAdapter(mAdapter);

        mSearchView = (SearchView)findViewById(R.id.sv_search_item);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: CHange ClassForm to dialog window
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
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
        startActivity(new Intent(this, ClassForm.class));
    }


}

package com.storagemanagement.app.scanshelf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.storagemanagement.app.scanshelf.data.SharedParameters;
import com.storagemanagement.app.scanshelf.database.AbstractManager;
import com.storagemanagement.app.scanshelf.database.AbstractRecord;
import com.storagemanagement.app.scanshelf.database.DBConnection;
import com.storagemanagement.app.scanshelf.database.DBContract;

import java.util.ArrayList;
import java.util.List;

public class ItemList extends Activity implements ItemListItemAdapter.ItemClickListener{

    private boolean isEditing = false;
    private FloatingActionButton mEditSave;
    private FloatingActionButton mAddItem;
    private FloatingActionButton mDeleteItem;

    private PopupWindow mAddItemWindow;
    private ConstraintLayout mItemListLayout;
    private ArrayAdapter<String> mCategoryAdapter;
    private ArrayAdapter<String> mNameAdapter;
    private Spinner mCategorySpinner;
    private Spinner mNameSpinner;

    private List<Integer> mSelectedItems;

    private DBConnection mDb;
    private AbstractManager mManager;
    private AbstractRecord mItemStack;

    private ItemListItemAdapter mAdapter;
    private RecyclerView mItemListView;
    private List<Item> mItemList;

    private List<Item> mBackupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        mEditSave = (FloatingActionButton)findViewById(R.id.fab_item_list_save_edit);
        mAddItem = (FloatingActionButton)findViewById(R.id.fab_item_list_add);
        mDeleteItem = (FloatingActionButton)findViewById(R.id.fab_item_list_delete);

        mItemListLayout = (ConstraintLayout)findViewById(R.id.cl_item_list);

        mSelectedItems = new ArrayList<>();
        mBackupList = new ArrayList<>();
        mItemListView = (RecyclerView)findViewById(R.id.rv_item_list);
        mItemList = new ArrayList<>();
        mItemList.add(new Item("horse","h",""));
        mItemList.add(new Item("cow","c",""));
        mItemList.add(new Item("cam","c",""));
        mItemList.add(new Item("sheep","s",""));
        mItemList.add(new Item("goat","g",""));
        //TODO; find all item in the location from database and add into item list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mItemListView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mItemListView.getContext(),layoutManager.getOrientation());
        mItemListView.addItemDecoration(dividerItemDecoration);

        mAdapter = new ItemListItemAdapter(this,mItemList);
        mAdapter.setClickListener(this);
        mItemListView.setAdapter(mAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME,MODE_PRIVATE);
        String stackUUID = sharedPreferences.getString(SharedParameters.OnDisplayParameters.STACK_UUID,"");

        //mDb = DBConnection.local();
        //mManager = mDb.getManager(DBContract.ITEM_STACK);
        //mItemStack = mManager.byUUID(stackUUID);

    }

    public void toggle(View view){
        if(isEditing){
            saveItemListEdit();
        }else{
            toggleToEdit();
            isEditing = true;
        }
    }

    private void toggleToEdit(){
        isEditing = true;
        mBackupList.clear();
        mBackupList.addAll(mItemList);
        mAddItem.setVisibility(View.VISIBLE);
        mDeleteItem.setVisibility(View.VISIBLE);
        mEditSave.setImageResource(android.R.drawable.ic_menu_save);
        //TODO: Add or Delete ItemStack
        //TODO: Add, Use or Dump ItemInstance

    }
    private void saveItemListEdit(){
        backToItemList();
        //TODO: Update ItemStack in database
        //TODO: Update ItemInstance in database
    }
    private void backToItemList(){
        isEditing = false;
        mItemList.clear();
        mItemList.addAll(mBackupList);
        mAdapter.notifyDataSetChanged();
        for(int i = 0; i<mItemList.size();i++){
            mItemListView.findViewHolderForAdapterPosition(i).itemView.setBackgroundColor(Color.WHITE);
        }
        mAddItem.setVisibility(View.GONE);
        mDeleteItem.setVisibility(View.GONE);
        mEditSave.setImageResource(android.R.drawable.ic_menu_edit);
    }

    @SuppressLint("InflateParams")
    public void toAddItem(){
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View createShelfWindow = null;
        if (inflater != null) {
            createShelfWindow = inflater.inflate(R.layout.popup_add_item,null);
        }
        mAddItemWindow = new PopupWindow(
                createShelfWindow,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAddItemWindow.setElevation(5);
        }
        //TODO: read category from database;
        List<String> categoryList = new ArrayList<>();
        categoryList.add("cata");
        categoryList.add("catb");
        categoryList.add("catc");
        mCategoryAdapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item,categoryList);
        mCategorySpinner = (Spinner)mAddItemWindow.getContentView().findViewById(R.id.sp_add_item_category);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<String> nameList = new ArrayList<>();
                nameList.add("caa");
                nameList.add("cab");
                nameList.add("ctc");
                mNameAdapter = new ArrayAdapter<String>(getParent(), R.layout.spinner_dropdown_item,nameList);
                mNameSpinner = (Spinner)mAddItemWindow.getContentView().findViewById(R.id.sp_add_item_name);
                mNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        mAddItemWindow.getContentView().findViewById(R.id.bt_add_item_add).setEnabled(true);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        mAddItemWindow.getContentView().findViewById(R.id.bt_add_item_add).setEnabled(false);
                    }
                });
                mNameSpinner.setClickable(true);
                mNameSpinner.setAdapter(mNameAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mNameSpinner.setClickable(false);
            }
        });

        mCategorySpinner.setAdapter(mCategoryAdapter);
        mAddItemWindow.showAtLocation(mItemListLayout, Gravity.TOP,0,0);
        mAddItemWindow.setFocusable(true);
        mAddItemWindow.update();
    }

    public void addItem(View view){
        toAddItem();
        //TODO: enable dropdown list to select category/name
    }

    public void toItemDetails(View view){
        SharedPreferences sharedPreferences = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME,MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SharedParameters.OnDisplayParameters.IS_FINDING_A_LOCATION,false).apply();
        startActivity(new Intent(this, ItemDetails.class));
    }

    @Override
    public void onItemClick(View view, int position) {
        if(isEditing){
            if(mSelectedItems.contains(position)){
                view.setBackgroundColor(Color.WHITE);
                mSelectedItems.remove(position);
            }else{
                mSelectedItems.add(position);
                view.setBackgroundColor(Color.CYAN);
            }

        }else{
            startActivity(new Intent(this, ItemDetails.class));
        }

    }

    @Override
    public void onBackPressed() {
        if(isEditing){
            backToItemList();
        }else{
            finish();
        }
    }

    public void delete(View view){
        for(int i = 0; i<mSelectedItems.size();i++){
            mItemListView.findViewHolderForAdapterPosition(i).itemView.setBackgroundColor(Color.WHITE);
        }
        for(int i = 0; i<mSelectedItems.size();i++){
            mItemList.remove((int)mSelectedItems.get(i));
        }

        mSelectedItems.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void add(View view){
        if(mCategorySpinner.isSelected()&&mNameSpinner.isSelected()){
            //TODO: add item to list and database;
            //TODO: refresh item list
            mAddItemWindow.dismiss();
            return;
        }
        Toast.makeText(this,"Please select item.",Toast.LENGTH_SHORT);
    }

    public void cancel(View view){
        mAddItemWindow.dismiss();
    }

}

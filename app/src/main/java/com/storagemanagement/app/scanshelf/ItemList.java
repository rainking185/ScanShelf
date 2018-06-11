package com.storagemanagement.app.scanshelf;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.storagemanagement.app.scanshelf.data.SharedParameters;
import com.storagemanagement.app.scanshelf.database.AbstractManager;
import com.storagemanagement.app.scanshelf.database.AbstractRecord;
import com.storagemanagement.app.scanshelf.database.DBConnection;
import com.storagemanagement.app.scanshelf.database.DBContract;

public class ItemList extends Activity {

    private boolean isEditing = false;
    private FloatingActionButton mEditSave;

    private DBConnection mDb;
    private AbstractManager mManager;
    private AbstractRecord mItemStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        mEditSave = (FloatingActionButton)findViewById(R.id.fab_item_details_save_edit);

        SharedPreferences sharedPreferences = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME,MODE_PRIVATE);
        String stackUUID = sharedPreferences.getString(SharedParameters.OnDisplayParameters.STACK_UUID,"");

        //mDb = DBConnection.local();
        //mManager = mDb.getManager(DBContract.ITEM_STACK);
        //mItemStack = mManager.byUUID(stackUUID);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if(isEditing){
                isEditing = false;
                backToItemList();
            } else{
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
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
        mEditSave.setImageResource(android.R.drawable.ic_menu_edit);
    }

    public void addItem(View view){
        //TODO: enable dropdown list to select category/name
    }

    public void toItemDetails(View view){
        SharedPreferences sharedPreferences = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME,MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SharedParameters.OnDisplayParameters.IS_FINDING_A_LOCATION,false).apply();
        startActivity(new Intent(this, ItemDetails.class));
    }
}

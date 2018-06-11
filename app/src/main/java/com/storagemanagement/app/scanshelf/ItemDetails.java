package com.storagemanagement.app.scanshelf;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.storagemanagement.app.scanshelf.data.SharedParameters;
import com.storagemanagement.app.scanshelf.database.AbstractManager;
import com.storagemanagement.app.scanshelf.database.AbstractRecord;
import com.storagemanagement.app.scanshelf.database.DBConnection;
import com.storagemanagement.app.scanshelf.database.DBContract;

import org.json.JSONArray;

public class ItemDetails extends Activity {

    private boolean hasLocationList = false;
    private boolean isFindingAnItem = true;
    private boolean isEditingRemark = false;
    private boolean isEditing = false;
    private FloatingActionButton mEditSave;
    private RecyclerView mLocationList;
    private LinearLayout mRemark;
    private ConstraintLayout mDetailDisplay;
    private ConstraintLayout mDetailEdit;
    private PopupWindow mEditRemark;
    private TextView mRemarkContent;
    private EditText mRemarkEdit;
    private AlertDialog remarkFragment;
    private static final String DIALOG_TAG = "remark";
    private Button tempSelectLocation;

    private DBConnection mDb;
    private AbstractManager mManager;
    private AbstractRecord mItemInstance;

    //TODO: Read from database according to ItemInstance with single or multiple location.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        //mDb = DBConnection.local();
        //mManager = mDb.getManager(DBContract.ITEM_INSTANCE);

        mRemarkContent = (TextView)findViewById(R.id.tv_item_details_remark);

        mEditSave = (FloatingActionButton)findViewById(R.id.fab_item_details_save_edit);
        mLocationList = (RecyclerView)findViewById(R.id.rv_location_list);
        tempSelectLocation = (Button)findViewById(R.id.temp_select_location);
        mRemark = (LinearLayout)findViewById(R.id.ll_item_details_remark);
        mDetailDisplay = (ConstraintLayout)findViewById(R.id.cl_item_details_display);
        mDetailEdit = (ConstraintLayout)findViewById(R.id.cl_item_details_edit_class);
        SharedPreferences sharedPreferences = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME,MODE_PRIVATE);
        hasLocationList = sharedPreferences.getBoolean(SharedParameters.OnDisplayParameters.IS_FINDING_A_LOCATION,true);

        if(!hasLocationList){
            toItemAtOneLocation();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if(isFindingAnItem){
                if(isEditing){
                    backToItemDetails();
                } else{
                    finish();
                }
            } else {
                if(isEditingRemark){
                    isEditingRemark = false;
                    mEditRemark.dismiss();
                }else{
                    if(hasLocationList){
                        backToSelectLocation();
                    }else{
                        finish();
                    }
                }

            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void toggle(View view){
        if(isEditing){
            saveItemDetailsEdit();
        }else{
            toggleToEdit();
            isEditing = true;
        }
    }

    private void toggleToEdit(){
        isEditing = true;
        mEditSave.setImageResource(android.R.drawable.ic_menu_save);
        mDetailEdit.setVisibility(View.VISIBLE);
        mDetailDisplay.setVisibility(View.GONE);

    }
    private void saveItemDetailsEdit(){
        backToItemDetails();
        //TODO: update to ItemInstance
    }
    private void backToItemDetails(){
        isEditing = false;
        mEditSave.setImageResource(android.R.drawable.ic_menu_edit);
        mDetailEdit.setVisibility(View.GONE);
        mDetailDisplay.setVisibility(View.VISIBLE);
    }

    public void selectLocation(View view){
        toItemAtOneLocation();
    }

    private void toItemAtOneLocation(){
        mEditSave.setVisibility(View.GONE);
        tempSelectLocation.setVisibility(View.GONE);
        mLocationList.setVisibility(View.GONE);
        mRemark.setVisibility(View.VISIBLE);
        isFindingAnItem = false;
    }

    private void backToSelectLocation(){
        mEditSave.setVisibility(View.VISIBLE);
        tempSelectLocation.setVisibility(View.VISIBLE);
        mLocationList.setVisibility(View.VISIBLE);
        mRemark.setVisibility(View.GONE);
        isFindingAnItem = true;
    }


    public void editRemark(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mRemarkEdit = new EditText(this);
        builder.setView(mRemarkEdit)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isEditingRemark = false;
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isEditingRemark = false;
                        mRemarkContent.setText(mRemarkEdit.getText());
                        dialogInterface.dismiss();
                    }
                })
                .show();
        mRemarkEdit.setText(mRemarkContent.getText());
    }

}

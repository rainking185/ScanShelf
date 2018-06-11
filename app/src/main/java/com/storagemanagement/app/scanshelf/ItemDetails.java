package com.storagemanagement.app.scanshelf;

import android.annotation.SuppressLint;
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
import android.support.v7.widget.DividerItemDecoration;
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
import android.widget.Toast;

import com.storagemanagement.app.scanshelf.data.SharedParameters;
import com.storagemanagement.app.scanshelf.database.AbstractManager;
import com.storagemanagement.app.scanshelf.database.AbstractRecord;
import com.storagemanagement.app.scanshelf.database.DBConnection;
import com.storagemanagement.app.scanshelf.database.DBContract;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ItemDetails extends Activity implements LocationListItemAdapter.ItemClickListener{

    private boolean hasLocationList = false;
    private boolean isFindingAnItem = true;
    private boolean isEditing = false;
    private FloatingActionButton mEditSave;

    private LinearLayout mRemark;
    private ConstraintLayout mDetailDisplay;
    private ConstraintLayout mDetailEdit;
    private PopupWindow mEditRemark;
    private TextView mRemarkContent;
    private Button tempSelectLocation;
    private ConstraintLayout mItemDetails;

    private TextView mTVCategory;
    private TextView mTVName;
    private TextView mTVUnit;
    private TextView mTVDescription;

    private TextView mTVLocation;

    private EditText mETCategory;
    private EditText mETName;
    private EditText mETUnit;
    private EditText mETDescription;

    private DBConnection mDb;
    private AbstractManager mManager;
    private AbstractRecord mItemInstance;

    private LocationListItemAdapter mAdapter;
    private RecyclerView mLocationListView;
    private List<Location> mLocationList;

    //TODO: Read from database according to ItemInstance with single or multiple location.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        //mDb = DBConnection.local();
        //mManager = mDb.getManager(DBContract.ITEM_INSTANCE);

        mRemarkContent = (TextView)findViewById(R.id.tv_item_details_remark);

        mTVCategory = (TextView)findViewById(R.id.tv_item_details_category);
        mTVName = (TextView)findViewById(R.id.tv_item_details_name);
        mTVUnit = (TextView)findViewById(R.id.tv_item_details_unit);
        mTVDescription = (TextView)findViewById(R.id.tv_item_details_description);

        mTVLocation = (TextView)findViewById(R.id.tv_item_details_location);

        mETCategory = (EditText)findViewById(R.id.et_item_details_category);
        mETName = (EditText)findViewById(R.id.et_item_details_name);
        mETUnit = (EditText)findViewById(R.id.et_item_details_unit);
        mETDescription = (EditText)findViewById(R.id.et_item_details_description);

        mEditSave = (FloatingActionButton)findViewById(R.id.fab_item_details_save_edit);
        mLocationListView = (RecyclerView)findViewById(R.id.rv_location_list);
        tempSelectLocation = (Button)findViewById(R.id.temp_select_location);
        mRemark = (LinearLayout)findViewById(R.id.ll_item_details_remark);
        mDetailDisplay = (ConstraintLayout)findViewById(R.id.cl_item_details_display);
        mDetailEdit = (ConstraintLayout)findViewById(R.id.cl_item_details_edit_class);
        mItemDetails = (ConstraintLayout)findViewById(R.id.cl_item_details);
        SharedPreferences sharedPreferences = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME,MODE_PRIVATE);
        hasLocationList = sharedPreferences.getBoolean(SharedParameters.OnDisplayParameters.IS_FINDING_A_LOCATION,true);

        mLocationList = new ArrayList<>();
        mLocationList.add(new Location("horse",1,1));
        mLocationList.add(new Location("cow",2,2));
        mLocationList.add(new Location("cam",3,2));
        mLocationList.add(new Location("sheep",8,2));
        mLocationList.add(new Location("goat",2,1));
        //TODO; find all location of itemClass from database and add into location list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mLocationListView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mLocationListView.getContext(),layoutManager.getOrientation());
        mLocationListView.addItemDecoration(dividerItemDecoration);

        mAdapter = new LocationListItemAdapter(this,mLocationList);
        mAdapter.setClickListener(this);
        mLocationListView.setAdapter(mAdapter);


        if(!hasLocationList){
            Location dummyLocation = new Location("myShelf",1,2);
            toItemAtOneLocation(dummyLocation);
        }

    }


    public void toggle(View view){
        if(isEditing){
            if(mETCategory.getText().toString().isEmpty()||mETName.getText().toString().isEmpty()){
                Toast.makeText(this,"Please complete compulsory(*) items.",Toast.LENGTH_SHORT).show();
                return;
            }
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
        mETCategory.setText(mTVCategory.getText());
        mETName.setText(mTVName.getText());
        mETUnit.setText(mTVUnit.getText());
        mETDescription.setText(mTVDescription.getText());
        mDetailDisplay.setVisibility(View.GONE);

    }
    private void saveItemDetailsEdit(){
        mTVCategory.setText(mETCategory.getText());
        mTVName.setText(mETName.getText());
        mTVUnit.setText(mETUnit.getText());
        mTVDescription.setText(mETDescription.getText());
        backToItemDetails();
        //TODO: update to ItemInstance database
    }
    private void backToItemDetails(){
        isEditing = false;
        mEditSave.setImageResource(android.R.drawable.ic_menu_edit);
        mDetailEdit.setVisibility(View.GONE);
        mDetailDisplay.setVisibility(View.VISIBLE);
    }

    public void selectLocation(View view){
        Location dummyLocation = new Location("myShelf",1,2);
        toItemAtOneLocation(dummyLocation);
    }

    private void toItemAtOneLocation(Location location){
        mTVLocation.setText(location.getmShelfName()+"-Row#:"+location.getmRow()+"; Column#:"+location.getmColumn());
        //TODO: read itemstack remark from database
        mEditSave.setVisibility(View.GONE);
        tempSelectLocation.setVisibility(View.GONE);
        mLocationListView.setVisibility(View.GONE);
        mRemark.setVisibility(View.VISIBLE);
        isFindingAnItem = false;
    }

    private void backToSelectLocation(){
        mTVLocation.setText("");
        mEditSave.setVisibility(View.VISIBLE);
        tempSelectLocation.setVisibility(View.VISIBLE);
        mLocationListView.setVisibility(View.VISIBLE);
        mRemark.setVisibility(View.GONE);
        isFindingAnItem = true;
    }

    @SuppressLint("InflateParams")
    public void toEditRemark(){
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View createShelfWindow = null;
        if (inflater != null) {
            createShelfWindow = inflater.inflate(R.layout.popup_edit_remark,null);
        }
        mEditRemark = new PopupWindow(
                createShelfWindow,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mEditRemark.setElevation(5);
        }
        mEditRemark.showAtLocation(mItemDetails, Gravity.TOP,0,0);
        ((EditText)mEditRemark.getContentView().findViewById(R.id.et_edit_remark)).setText(mRemarkContent.getText());
        mEditRemark.setFocusable(true);
        mEditRemark.update();
    }

    public void save(View view){
        mRemarkContent.setText(((EditText)mEditRemark.getContentView().findViewById(R.id.et_edit_remark)).getText().toString());
        mEditRemark.dismiss();
        //TODO: Update remark in database.
    }

    public void cancel(View view){
        mEditRemark.dismiss();
    }

    public void editRemark(View view){
        toEditRemark();
    }

    @Override
    public void onItemClick(View view, int position) {
        Location location = mLocationList.get(position);
        toItemAtOneLocation(location);
    }

    @Override
    public void onBackPressed() {
        if(isFindingAnItem){
            if(isEditing){
                backToItemDetails();
            }else{
                finish();
            }
        }else{
            if(hasLocationList) {
                backToSelectLocation();
            }
            else{
                finish();
            }
        }
    }
}

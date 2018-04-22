package com.storagemanagement.app.scanshelf;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.storagemanagement.app.scanshelf.data.SharedParameters;
import com.storagemanagement.app.scanshelf.database.AbstractManager;
import com.storagemanagement.app.scanshelf.database.AbstractRecord;
import com.storagemanagement.app.scanshelf.database.DBConnection;
import com.storagemanagement.app.scanshelf.database.DBContract;

public class ShelfView extends AppCompatActivity{

    private static final int UNIT_CELL_SIZE = 60;

    private Button tempButton;

    private Button[][] mCellButton = new Button[8][6];
    private static final int MAX_ROW = 7;
    private static final int MAX_CELL = 5;
    private boolean isEditing = false;
    private FloatingActionButton mEditSave;
    private TextView mShelfNameTextView;
    private TextView mShelfNameEditText;
    private FloatingActionButton mAdd;
    private FloatingActionButton mDelete;

    private DBConnection mDb;
    private AbstractManager mShelfManager;
    private AbstractRecord mShelf;
    private AbstractManager mItemManager;

    private int mRowCount = 0;
    private int[] mCellCount = {0,0,0,0,0,0,0,0};
    private int[][] mCellSize = {
            {0,0,0,0,0,0},
            {0,0,0,0,0,0},
            {0,0,0,0,0,0},
            {0,0,0,0,0,0},
            {0,0,0,0,0,0},
            {0,0,0,0,0,0},
            {0,0,0,0,0,0},
            {0,0,0,0,0,0}
    };
    private int mTotalSize = 0;

    private final int[][] CELL_ID = {
            {R.id.bt_shelf_row_0_cell_0,R.id.bt_shelf_row_0_cell_1,R.id.bt_shelf_row_0_cell_2,R.id.bt_shelf_row_0_cell_3,R.id.bt_shelf_row_0_cell_4,R.id.bt_shelf_row_0_cell_5},
            {R.id.bt_shelf_row_1_cell_0,R.id.bt_shelf_row_1_cell_1,R.id.bt_shelf_row_1_cell_2,R.id.bt_shelf_row_1_cell_3,R.id.bt_shelf_row_1_cell_4,R.id.bt_shelf_row_1_cell_5},
            {R.id.bt_shelf_row_2_cell_0,R.id.bt_shelf_row_2_cell_1,R.id.bt_shelf_row_2_cell_2,R.id.bt_shelf_row_2_cell_3,R.id.bt_shelf_row_2_cell_4,R.id.bt_shelf_row_2_cell_5},
            {R.id.bt_shelf_row_3_cell_0,R.id.bt_shelf_row_3_cell_1,R.id.bt_shelf_row_3_cell_2,R.id.bt_shelf_row_3_cell_3,R.id.bt_shelf_row_3_cell_4,R.id.bt_shelf_row_3_cell_5},
            {R.id.bt_shelf_row_4_cell_0,R.id.bt_shelf_row_4_cell_1,R.id.bt_shelf_row_4_cell_2,R.id.bt_shelf_row_4_cell_3,R.id.bt_shelf_row_4_cell_4,R.id.bt_shelf_row_4_cell_5},
            {R.id.bt_shelf_row_5_cell_0,R.id.bt_shelf_row_5_cell_1,R.id.bt_shelf_row_5_cell_2,R.id.bt_shelf_row_5_cell_3,R.id.bt_shelf_row_5_cell_4,R.id.bt_shelf_row_5_cell_5},
            {R.id.bt_shelf_row_6_cell_0,R.id.bt_shelf_row_6_cell_1,R.id.bt_shelf_row_6_cell_2,R.id.bt_shelf_row_6_cell_3,R.id.bt_shelf_row_6_cell_4,R.id.bt_shelf_row_6_cell_5},
            {R.id.bt_shelf_row_7_cell_0,R.id.bt_shelf_row_7_cell_1,R.id.bt_shelf_row_7_cell_2,R.id.bt_shelf_row_7_cell_3,R.id.bt_shelf_row_7_cell_4,R.id.bt_shelf_row_7_cell_5}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf_view);
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDb = DBConnection.local();
        mShelfManager = mDb.getManager(DBContract.SHELF);
        mItemManager = mDb.getManager(DBContract.ITEM_STACK);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        ConstraintLayout shlefView = (ConstraintLayout) findViewById(R.id.cl_shelf_view);
        tempButton = new Button(this);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ItemList.class);
                startActivity(intent);
            }
        });
        shlefView.addView(tempButton,layoutParams);
        tempButton.setPadding(16,16,16,16);


        mEditSave = (FloatingActionButton)findViewById(R.id.fab_save_edit);

        mShelfNameEditText = (EditText)findViewById(R.id.et_shelf_name);
        mShelfNameTextView = (TextView)findViewById(R.id.tv_shelf_name);
        for(int i = 0; i <= MAX_ROW; i++){
            for(int j = 0; j <= MAX_CELL;j++){
                mCellButton[i][j] = (Button)findViewById(CELL_ID[i][j]);
            }
        }
        mAdd = (FloatingActionButton)findViewById(R.id.fab_shlef_add_cell);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                if(mTotalSize < MAX_CELL){
                    mCellSize[mRowCount][mCellCount[mRowCount]]++;
                    mTotalSize++;
                    mCellButton[mRowCount][mCellCount[mRowCount]].setWidth(mCellButton[mRowCount][mCellCount[mRowCount]].getWidth()+getPx(UNIT_CELL_SIZE));
                }else{
                    mRowCount++;
                    mTotalSize = 0;
                    mCellButton[mRowCount][mCellCount[mRowCount]].setVisibility(View.VISIBLE);
                }
                if(mRowCount == MAX_ROW && mTotalSize == MAX_CELL){
                    mAdd.setEnabled(false);
                }
                mDelete.setEnabled(true);
            }
        });
        mAdd.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onLongClick(View view) {
                if(mTotalSize < MAX_CELL){
                    mCellCount[mRowCount]++;
                    mTotalSize++;
                    mCellButton[mRowCount][mCellCount[mRowCount]].setVisibility(View.VISIBLE);
                }else{
                    mRowCount++;
                    mTotalSize = 0;
                    mCellButton[mRowCount][mCellCount[mRowCount]].setVisibility(View.VISIBLE);
                }
                if(mRowCount == MAX_ROW && mTotalSize == MAX_CELL){
                    mAdd.setEnabled(false);
                }
                mDelete.setEnabled(true);
                return true;
            }
        });

        mDelete = (FloatingActionButton)findViewById(R.id.fab_shelf_delete_cell);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                if(mCellSize[mRowCount][mCellCount[mRowCount]]>0){
                    mCellButton[mRowCount][mCellCount[mRowCount]].setWidth(mCellButton[mRowCount][mCellCount[mRowCount]].getWidth()-getPx(UNIT_CELL_SIZE));
                    mCellSize[mRowCount][mCellCount[mRowCount]]--;
                    mTotalSize--;
                }else{
                    mCellButton[mRowCount][mCellCount[mRowCount]].setVisibility(View.GONE);
                    if(mTotalSize > 0){
                        mTotalSize--;
                        mCellCount[mRowCount]--;
                    }else{
                        mTotalSize = MAX_CELL;
                        mRowCount--;
                    }
                }
                mAdd.setEnabled(true);
                if(mTotalSize == 0&&mRowCount == 0){
                    mDelete.setEnabled(false);
                }
            }
        });
        mDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(mCellCount[mRowCount]>0){
                    mTotalSize = mTotalSize - mCellSize[mRowCount][mCellCount[mRowCount]]-1;
                    mCellButton[mRowCount][mCellCount[mRowCount]].setWidth(getPx(60));
                    mCellButton[mRowCount][mCellCount[mRowCount]].setVisibility(View.GONE);
                    mCellSize[mRowCount][mCellCount[mRowCount]] = 0;
                    mCellCount[mRowCount]--;
                }else{
                    mTotalSize = 0;
                    mCellSize[mRowCount][mCellCount[mRowCount]] = 0;
                    mCellButton[mRowCount][mCellCount[mRowCount]].setWidth(getPx(60));
                    if(mRowCount > 0){
                        mCellButton[mRowCount][mCellCount[mRowCount]].setVisibility(View.GONE);
                        mTotalSize = MAX_CELL;
                        mRowCount--;
                    }
                }
                mAdd.setEnabled(true);
                if(mTotalSize == 0&&mRowCount == 0){
                    mDelete.setEnabled(false);
                }
                return true;
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME,MODE_PRIVATE);
        boolean shelfIsNew = sharedPreferences.getBoolean(SharedParameters.OnDisplayParameters.SHELF_IS_NEW,false);

        if(shelfIsNew){
            sharedPreferences.edit().putBoolean(SharedParameters.OnDisplayParameters.SHELF_IS_NEW,false);
            toggleToEdit();
            isEditing = true;
            mShelfNameEditText.setText("My Shelf");
        }else{
            mShelf = mShelfManager.byUUID(sharedPreferences.getString(SharedParameters.OnDisplayParameters.QR_CODE,""));
            mEditSave.setVisibility(View.GONE);
            mShelfNameTextView.setText(mShelf.getName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if(isEditing){
                isEditing = false;
                backToShelfView();
            } else{
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggle(View view){
        if(isEditing){
            saveShelfEdit();
        }else{
            toggleToEdit();
            isEditing = true;
        }
    }

    private void toggleToEdit(){
        isEditing = true;
        mEditSave.setImageResource(android.R.drawable.ic_menu_save);
        mAdd.setVisibility(View.VISIBLE);
        mDelete.setVisibility(View.VISIBLE);
        mShelfNameEditText.setVisibility(View.VISIBLE);
        mShelfNameTextView.setVisibility(View.GONE);

        for(int i = 0; i < MAX_ROW; i++){
            for(int j = 0; j < MAX_CELL;j++){
                mCellButton[i][j].setEnabled(false);
            }
        }
    }
    private void saveShelfEdit(){
        String shelfName = mShelfNameEditText.getText().toString();
        mShelfNameTextView.setText(shelfName);
        mShelf.setName(shelfName);
        //TODO: Create ItemStacks
        //TODO: Update ItemStacks and CellSize
        backToShelfView();
    }
    private void backToShelfView(){
        isEditing = false;
        mEditSave.setImageResource(android.R.drawable.ic_menu_edit);
        mAdd.setVisibility(View.GONE);
        mDelete.setVisibility(View.GONE);
        mShelfNameEditText.setVisibility(View.GONE);
        mShelfNameTextView.setVisibility(View.VISIBLE);
        for(int i = 0; i < MAX_ROW; i++){
            for(int j = 0; j < MAX_CELL;j++){
                mCellButton[i][j].setEnabled(true);
            }
        }
    }

    public int getPx(int dp){
        float scale = getResources().getDisplayMetrics().density;
        return((int) (dp * scale + 0.5f));
    }

}

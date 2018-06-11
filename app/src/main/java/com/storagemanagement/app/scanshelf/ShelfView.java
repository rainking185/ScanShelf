package com.storagemanagement.app.scanshelf;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.storagemanagement.app.scanshelf.data.SharedParameters;
import com.storagemanagement.app.scanshelf.database.AbstractManager;
import com.storagemanagement.app.scanshelf.database.AbstractRecord;
import com.storagemanagement.app.scanshelf.database.DBConnection;
import com.storagemanagement.app.scanshelf.database.DBContract;

public class ShelfView extends Activity{

    private PopupWindow mDeleteShelf;

    private Button tempButton;

    private LinearLayout mShelfView;
    private TextView mShelfNameDisplay;
    private String mShelfName="MyShelf";
    private int mRow=9;
    private int mColumn=9;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf_view);

        //TODO: read shelf name, row and column from database.

        View.OnClickListener onSectionClicked = view -> {
            int name = Integer.parseInt(((Button)view).getText().toString());
            //TODO; read itemstack ID from database
            Intent intent = new Intent(view.getContext(), ItemList.class);
            startActivity(intent);
        };

        mShelfNameDisplay = (TextView)findViewById(R.id.tv_shelf_name);
        mShelfNameDisplay.setText(mShelfName);

        mShelfView = (LinearLayout)findViewById(R.id.ll_shelf_view);
        int height = getPx(360)/mRow;
        int width = getPx(315)/mColumn;
        for(int i = 0;i<mRow;i++){
            LinearLayout newRow = new LinearLayout(this);
            newRow.setOrientation(LinearLayout.HORIZONTAL);
            newRow.setGravity(View.TEXT_ALIGNMENT_CENTER);
            newRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            for(int j = 0;j<mColumn;j++){
                TextView newColumn = new TextView(this);
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams.setMargins(1,1,1,1);
                newColumn.setLayoutParams(lparams);
                newColumn.setText(i+""+j+"");
                newColumn.setHeight(height);
                newColumn.setWidth(width);
                newColumn.setBackgroundColor(Color.RED);
                newColumn.setOnClickListener(onSectionClicked);
                newColumn.setGravity(TextView.TEXT_ALIGNMENT_CENTER);
                newRow.addView(newColumn);
            }
            mShelfView.addView(newRow);
        }

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

        SharedPreferences sharedPreferences = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME,MODE_PRIVATE);
        boolean shelfIsNew = sharedPreferences.getBoolean(SharedParameters.OnDisplayParameters.SHELF_IS_NEW,false);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    public int getPx(int dp){
        float scale = getResources().getDisplayMetrics().density;
        return((int) (dp * scale + 0.5f));
    }

    public void deleteShelf(View view){
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View createShelfWindow = inflater.inflate(R.layout.popup_delete_shelf,null);
        mDeleteShelf= new PopupWindow(
                createShelfWindow,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDeleteShelf.setElevation(5);
        }
        mDeleteShelf.showAtLocation(findViewById(R.id.cl_shelf_view), Gravity.CENTER,0,0);
    }

    public void yes(View view){
        //TODO: delete shelf in database;
        mDeleteShelf.dismiss();
        finish();
    }

    public void no(View view){
        mDeleteShelf.dismiss();
    }

}

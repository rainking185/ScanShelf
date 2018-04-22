package com.storagemanagement.app.scanshelf;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.storagemanagement.app.scanshelf.database.AbstractManager;
import com.storagemanagement.app.scanshelf.database.DBConnection;
import com.storagemanagement.app.scanshelf.database.DBContract;
import com.storagemanagement.app.scanshelf.database.ItemClass;

import org.json.JSONArray;

public class ClassForm extends AppCompatActivity {

    private EditText mCategory;
    private EditText mName;
    private EditText mUnit;
    private EditText mDescription;

    private DBConnection mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_form);

        mDb = DBConnection.local();

        mCategory = (EditText)findViewById(R.id.et_class_form_category);
        mName = (EditText)findViewById(R.id.et_class_form_item_name);
        mUnit = (EditText)findViewById(R.id.et_class_form_unit);
        mDescription = (EditText)findViewById(R.id.et_class_form_description);

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if(id == R.id.action_save){
            if(mCategory.getText().toString().equals("")||mName.getText().toString().equals("")||mUnit.getText().toString().equals("")){
                Toast.makeText(this,"Please complete compulsory(*) items.",Toast.LENGTH_LONG).show();
                return false;
            }
            mDb.itemClasses.insert(mName.getText().toString());
            //TODO: add category unit description
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

}

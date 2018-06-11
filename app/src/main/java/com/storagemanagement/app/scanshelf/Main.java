package com.storagemanagement.app.scanshelf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.storagemanagement.app.scanshelf.barcode.BarcodeGraphic;
import com.storagemanagement.app.scanshelf.barcode.BarcodeGraphicTracker;
import com.storagemanagement.app.scanshelf.barcode.BarcodeTrackerFactory;
import com.storagemanagement.app.scanshelf.camera.CameraSource;
import com.storagemanagement.app.scanshelf.camera.CameraSourcePreview;
import com.storagemanagement.app.scanshelf.camera.GraphicOverlay;
import com.storagemanagement.app.scanshelf.data.SharedParameters;
import com.storagemanagement.app.scanshelf.database.AbstractManager;
import com.storagemanagement.app.scanshelf.database.DBConnection;
import com.storagemanagement.app.scanshelf.database.DBContract;

import java.io.File;
import java.io.IOException;

public class Main extends Activity implements BarcodeGraphicTracker.BarcodeUpdateListener{

    private ConstraintLayout mMain;

    private PopupWindow mCreateShelf;

    private CameraSourcePreview mCameraView;
    private CameraSource mCameraSource;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    private String mQRCode;
    private TextView mCodeObtained;

    private Button mConfirmCode;
    private Button mScanAgain;

    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private boolean creatingShelf = false;

    private  DBConnection mDb;
    private File mFile;
    private AbstractManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mFile = new File(DBConnection.defaultDBFileName);
        //mDb = DBConnection.local(); //TODO: ERROR: THIS FUNCTION RETURNS NULL
        //if (!mFile.isFile()) mDb.createTables();
        //mManager = mDb.getManager(DBContract.SHELF);

        mMain = (ConstraintLayout)findViewById(R.id.cl_main);
        mCameraView = (CameraSourcePreview)findViewById(R.id.csp_main);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>)findViewById(R.id.go_main);
        mCodeObtained = (TextView)findViewById(R.id.tv_qr_code);
        mConfirmCode = (Button)findViewById(R.id.bt_confirm_code);
        mScanAgain = (Button)findViewById(R.id.bt_scan_again);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else {
            createCameraSource();
        }
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = view -> ActivityCompat.requestPermissions(thisActivity, permissions,
                RC_HANDLE_CAMERA_PERM);

        findViewById(R.id.cl_main).setOnClickListener(listener);
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    private void createCameraSource(){
        Context context = getApplicationContext();

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeTrackerFactory;
        barcodeTrackerFactory = new BarcodeTrackerFactory(mGraphicOverlay, this);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeTrackerFactory).build());

        if(!barcodeDetector.isOperational()){
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null,lowStorageFilter)!=null;
            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
            }
        }

        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(),barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600,1024)
                .setRequestedFps(15);

        builder = builder.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        mCameraSource = builder.setFlashMode(null).build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeObtained.setText("scanning...");
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mCameraView!=null){
            mCameraView.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCameraView!=null){
            mCameraView.release();
        }
    }

    private void startCameraSource() throws SecurityException
    {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mCameraView.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }
    public void toSearchActivity(View view){
        startActivity(new Intent(this,SearchItem.class));
    }


    public void create(View view){
        if(!creatingShelf){
            runOnUiThread(() -> {
                mCreateShelf.getContentView().findViewById(R.id.ll_create_shelf_detail).setVisibility(View.VISIBLE);
                creatingShelf = true;
            });
        }
        else{
            String name = ((EditText)mCreateShelf.getContentView().findViewById(R.id.et_create_Shelf_name)).getText().toString();
            if (name.isEmpty()){
                Toast.makeText(this,"Name not entered.",Toast.LENGTH_SHORT).show();
                return;
            }
            int row;
            try{
                row = Integer.parseInt(((EditText)mCreateShelf.getContentView().findViewById(R.id.et_create_Shelf_row)).getText().toString());
            }
            catch (Exception e){
                Toast.makeText(this,"Invalid Row #.",Toast.LENGTH_SHORT).show();
                return;
            }
            if(row<1||row>10){
                Toast.makeText(this,"Row # must be between 1 and 10.",Toast.LENGTH_SHORT).show();
                return;
            }
            int column;
            try{
                column = Integer.parseInt(((EditText)mCreateShelf.getContentView().findViewById(R.id.et_create_Shelf_column)).getText().toString());
            }
            catch (Exception e){
                Toast.makeText(this,"Invalid Column #.",Toast.LENGTH_SHORT).show();
                return;
            }
            if(column<1||column>10){
                Toast.makeText(this,"Column # must be between 1 and 10.",Toast.LENGTH_SHORT).show();
                return;
            }
            //TODO: Register new shelf to database
            SharedPreferences sharedPreferences = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME,MODE_PRIVATE);
            sharedPreferences.edit().putString(SharedParameters.OnDisplayParameters.QR_CODE,"").apply();
            startActivity(new Intent(this,ShelfView.class));
            mCreateShelf.dismiss();
            creatingShelf=false;
        }
    }

    @SuppressLint("InflateParams")
    public void toCreateShelf(){
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View createShelfWindow = null;
        if (inflater != null) {
            createShelfWindow = inflater.inflate(R.layout.popup_create_shelf,null);
        }
        mCreateShelf = new PopupWindow(
                createShelfWindow,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCreateShelf.setElevation(5);
        }
        mCreateShelf.showAtLocation(mMain, Gravity.TOP,0,0);
        mCreateShelf.setFocusable(true);
        mCreateShelf.update();

    }

    public void cancel(View view){
        mCreateShelf.dismiss();
        creatingShelf=false;
    }

    @Override
    public void onBarcodeDetected(Barcode barcode) {
        mQRCode = barcode.rawValue;
        runOnUiThread(() -> {
            mCodeObtained.setText(mQRCode);
            mScanAgain.setEnabled(true);
            mConfirmCode.setEnabled(true);
            mCameraView.stop();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        DialogInterface.OnClickListener listener = (dialog, id) -> finish();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    public void scan(View view){
        runOnUiThread(() -> {
            String text = "scanning...";
            mCodeObtained.setText(text);
            mScanAgain.setEnabled(false);
            mConfirmCode.setEnabled(false);
            startCameraSource();
        }
        );
    }

    public void confirm(View view){
        toCreateShelf();
    }

}

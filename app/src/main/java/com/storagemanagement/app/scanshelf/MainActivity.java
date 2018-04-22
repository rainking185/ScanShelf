package com.storagemanagement.app.scanshelf;

import android.Manifest;
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
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.storagemanagement.app.scanshelf.camera.CameraSource;
import com.storagemanagement.app.scanshelf.camera.CameraSourcePreview;
import com.storagemanagement.app.scanshelf.camera.GraphicOverlay;
import com.storagemanagement.app.scanshelf.data.SharedParameters;
import com.storagemanagement.app.scanshelf.database.AbstractManager;
import com.storagemanagement.app.scanshelf.database.AbstractRecord;
import com.storagemanagement.app.scanshelf.database.DBConnection;
import com.storagemanagement.app.scanshelf.database.DBContract;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements BarcodeGraphicTracker.BarcodeUpdateListener{

    private ConstraintLayout mMain;

    private PopupWindow mCreateShelf;

    private CameraSourcePreview mCameraView;
    private CameraSource mCameraSource;
    private GraphicOverlay mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private File mFile;
    private DBConnection mDb;
    private AbstractManager mManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFile = new File(DBConnection.defaultDBFileName);
        mDb = DBConnection.local();
        if (!mFile.isFile()) mDb.createTables();
        mManager = mDb.getManager(DBContract.SHELF);

        mMain = (ConstraintLayout)findViewById(R.id.cl_main);
        mCameraView = (CameraSourcePreview)findViewById(R.id.csp_main);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>)findViewById(R.id.go_main);

        boolean autoFocus = true;
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus);
        } else {
            requestCameraPermission();
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

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        findViewById(R.id.cl_main).setOnClickListener(listener);
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    private void createCameraSource(boolean autoFocus){
        Context context = getApplicationContext();

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeTrackerFactory = new BarcodeTrackerFactory(mGraphicOverlay, this);
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

        builder = builder.setFocusMode(autoFocus? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE:null);

        mCameraSource = builder.setFlashMode(false? Camera.Parameters.FLASH_MODE_AUTO:null).build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedParameters = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME, Context.MODE_PRIVATE);
        if(!(sharedParameters.getString(SharedParameters.OnDisplayParameters.QR_CODE,"").isEmpty())){
            startActivity(new Intent(this,ShelfView.class));
        }else{
            startCameraSource();
        }
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
        finish();
    }


    public void create(View view){
        SharedPreferences sharedPreferences = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME,MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SharedParameters.OnDisplayParameters.SHELF_IS_NEW,true).apply();
        startActivity(new Intent(this,ShelfView.class));
        mCreateShelf.dismiss();
    }

    public void toCreateShelf(){

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View createShelfWindow = inflater.inflate(R.layout.popup_create_shelf,null);
        mCreateShelf = new PopupWindow(
                createShelfWindow,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCreateShelf.setElevation(5);
        }
        mCreateShelf.showAtLocation(mMain, Gravity.CENTER,0,0);

    }

    public void cancel(View view){
        SharedPreferences sharedParameters = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME, Context.MODE_PRIVATE);
        sharedParameters.edit().putString(SharedParameters.OnDisplayParameters.QR_CODE,"").apply();
        mCreateShelf.dismiss();
    }

    @Override
    public void onBarcodeDetected(Barcode barcode) {
        String qrCode = barcode.rawValue;
        SharedPreferences sharedParameters = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME,MODE_PRIVATE);
        sharedParameters.edit().putString(SharedParameters.OnDisplayParameters.QR_CODE,qrCode).apply();

        AbstractRecord shelf = mManager.byUUID(qrCode);

        if(shelf != null){
            SharedPreferences sharedPreferences = getSharedPreferences(SharedParameters.OnDisplayParameters.NAME,MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(SharedParameters.OnDisplayParameters.SHELF_IS_NEW,false).apply();
            startActivity(new Intent(this,ShelfView.class));
        } else{
            toCreateShelf();
        }

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
            boolean autoFocus = true;
            createCameraSource(autoFocus);
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

}

package com.inihood.photoly.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.muddzdev.styleabletoast.StyleableToast;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.io.File;
import java.util.List;
import java.util.Objects;

import ly.img.android.ui.activities.CameraPreviewActivity;
import ly.img.android.ui.activities.CameraPreviewIntent;
import ly.img.android.ui.activities.PhotoEditorIntent;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_CODE = 2;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    public static int CAMERA_PREVIEW_RESULT = 1;
    private CardView cameraBtn, liveCameraBtn, editorBtn;
    private String path;
    private File dir;
    private long mBackPressed;
    private AlertDialog alertDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        View toolbar = findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) toolbar);
        (getSupportActionBar()).setDisplayShowTitleEnabled(false);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        StartAppSDK.init(this, getString(R.string.startapp_id), false);
        StartAppSDK.setUserConsent(this,
                "pas",
                System.currentTimeMillis(),
                true);
        StartAppSDK.setUserConsent(this,
                "pas",
                System.currentTimeMillis(),
                false);
        StartAppAd.disableSplash();
        StartAppAd.disableAutoInterstitial();


       // cameraBtn =  findViewById(R.id.camera);
        editorBtn =  findViewById(R.id.editor);
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.directory_name);
        dir = new File(path);
        CheckorRequestPermissions();

//        cameraBtn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN: {
//                        CardView view = (CardView) v;
//                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
//                        v.invalidate();
//                        break;
//                    }
//                    case MotionEvent.ACTION_UP:
//
//                        // Your action here on button click
//                        new CameraPreviewIntent(MainActivity.this)
//                                .setExportDir(dir.getPath())
//                                .setExportPrefix(getString(R.string.photo_preview_prefix))
//                                .setEditorIntent(
//                                        new PhotoEditorIntent(MainActivity.this)
//                                                .setExportDir(dir.getPath())
//                                                .setExportPrefix(getString(R.string.photo_result_prefix))
//                                                .destroySourceAfterSave(true)
//                                )
//                                .startActivityForResult(CAMERA_PREVIEW_RESULT);
//
//                    case MotionEvent.ACTION_CANCEL: {
//                        CardView view = (CardView) v;
//                        view.getBackground().clearColorFilter();
//                        view.invalidate();
//                        break;
//                    }
//                }
//                return true;
//            }
//        });

//        cameraBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new CameraPreviewIntent(MainActivity.this)
//                        .setExportDir(dir.getPath())
//                        .setExportPrefix(getString(R.string.photo_preview_prefix))
//                        .setEditorIntent(
//                                new PhotoEditorIntent(MainActivity.this)
//                                        .setExportDir(dir.getPath())
//                                        .setExportPrefix(getString(R.string.photo_result_prefix))
//                                        .destroySourceAfterSave(true)
//                        )
//                        .startActivityForResult(CAMERA_PREVIEW_RESULT);
//            }
//        });


        editorBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction())

                {
                    case MotionEvent.ACTION_DOWN: {
                        CardView view = (CardView) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                        new ImagePicker.Builder(MainActivity.this)
                                .mode(ImagePicker.Mode.GALLERY)
                                .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                                .directory(ImagePicker.Directory.DEFAULT)
                                .extension(ImagePicker.Extension.PNG)
                                .scale(600, 600)
                                .allowMultipleImages(false)
                                .enableDebuggingMode(true)
                                .build();

                    case MotionEvent.ACTION_CANCEL: {
                        CardView view = (CardView) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }

                }
                return false;
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_PREVIEW_RESULT) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String path = data.getStringExtra(CameraPreviewActivity.RESULT_IMAGE_PATH);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
            StyleableToast.makeText(this, getString(R.string.photo_saved_toast_string), Toast.LENGTH_LONG, R.style.mytoastt).show();

        }
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            List<String> mPaths = (List<String>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH);
            new PhotoEditorIntent(MainActivity.this)
                    .setSourceImagePath(mPaths.get(0))
                    .setExportDir(dir.getPath())
                    .setExportPrefix(getString(R.string.photo_result_prefix))
                    .destroySourceAfterSave(true)
                    .startActivityForResult(CAMERA_PREVIEW_RESULT);
        }
    }


    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            StyleableToast.makeText(this, getString(R.string.exite_text), Toast.LENGTH_LONG, R.style.mytoastt).show();
        }

        mBackPressed = System.currentTimeMillis();
    }


    public boolean CheckorRequestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
                return false;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setMessage(getString(R.string.permissions_message));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                            CheckorRequestPermissions();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                            Toast.makeText(MainActivity.this, getString(R.string.toast_message), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    alertDialog.show();
                }
                return;
            }
        }
    }
}

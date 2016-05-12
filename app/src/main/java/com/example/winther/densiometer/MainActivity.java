package com.example.winther.densiometer;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button takePicture, releasePreview, acceptButton;

    private static Camera mCamera;
    private Preview mPreview;

    private final static int COUNT_OF_SQUARES = 20;

    private static String filenameOfPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create an instance of Camera
        mCamera = getCameraInstance();

        //Create our Preview view and set it as the content of our activity
        mPreview = new Preview(this, mCamera);

        FrameLayout picturePreview = (FrameLayout) findViewById(R.id.camera_preview);
        picturePreview.addView(mPreview);


        // Button for taking a picture.
        takePicture = (Button) findViewById(R.id.button_capture);
        takePicture.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );

        // Button for releasing the preview
        releasePreview = (Button) findViewById(R.id.release_preview);
        releasePreview.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mCamera.startPreview();
                    }
                }
        );

        // Button for accepting current picture and processing it
        acceptButton = (Button) findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ImageCalculatorActivity.class);
                        intent.putExtra("filename", filenameOfPath);
                        startActivityForResult(intent, COUNT_OF_SQUARES);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_photo:
                // User chose the "Settings" item, show the app settings UI...
                intent = new Intent(this, ImageCalculatorActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_result_list:
                // User chose the "Settings" item, show the app settings UI...
                intent = new Intent(this, ResultListActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera cam = null;
        try {
            cam = Camera.open(1); // Opens front-camera (value 1)
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.d("Class:Main", e.getMessage());
        }
        return cam; // returns null if camera is unavailable
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null){
                Log.d("Class:Main", "Error creating media file, check storage permissions:");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("Class:Main", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("Class:Main", "Error accessing file: " + e.getMessage());
            }
        }
    };

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DensiometerApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Class:Main", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        filenameOfPath = mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg";
        mediaFile = new File(filenameOfPath);


        return mediaFile;
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(directory, "densiometer.jpg");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == COUNT_OF_SQUARES) {
            //do something
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}


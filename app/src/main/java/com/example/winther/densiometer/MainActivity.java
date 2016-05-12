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

import com.example.winther.densiometer.util.ImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button takePicture, releasePreview, acceptButton;
    private Preview mPreview;

    private final static int COUNT_OF_SQUARES = 20;

    private static String filenameOfPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create an instance of Camera
        Camera mCamera = getCameraInstance();

        //Create our Preview view and set it as the content of our activity
        mPreview = new Preview(this, mCamera);

        FrameLayout picturePreview = (FrameLayout) findViewById(R.id.camera_preview);
        picturePreview.addView(mPreview);


        // Button for taking a picture.
        takePicture = (Button) findViewById(R.id.button_capture);

        // Button for releasing the preview
        releasePreview = (Button) findViewById(R.id.release_preview);

        // Button for accepting current picture and processing it
        acceptButton = (Button) findViewById(R.id.accept_button);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register onClickListeners again (with the camera from mPreview
        takePicture.setOnClickListener(
            new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // get an image from the camera
                    mPreview.getCamera().takePicture(null, null, mPicture);
                }
            }
        );

        releasePreview.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPreview.getCamera().startPreview();
                }
            }
        );

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
            case R.id.action_result_list:
                intent = new Intent(this, ResultListActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera cam = null;
        try {
            cam = Camera.open(1); // Opens front-camera (value 1)
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.d("Class:Main", e.getMessage());
        }
        return cam; // returns null if camera is unavailable
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = ImageUtils.getOutputMediaFile();
            if (pictureFile == null) {
                Log.d("Class:Main", "Error creating media file, check storage permissions:");
                return;
            }
            filenameOfPath = pictureFile.getAbsolutePath();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COUNT_OF_SQUARES) {
            //do something
        }
    }
}


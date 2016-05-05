package com.example.winther.densiometer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

public class ImageCalculatorActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CODE = 4321;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_calculator);

        Button button = (Button) findViewById(R.id.pick_image);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_IMAGE_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Did we get an image?
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CODE) {
            Uri chosenImageUri = data.getData();

            Bitmap mBitmap = null;
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ImageView imageView = (ImageView) findViewById(R.id.image_view_for_processing);
            imageView.setImageBitmap(mBitmap);

            new ProcessImageTask().execute(mBitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private class ProcessImageTask extends AsyncTask<Bitmap, Void, Void> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ImageCalculatorActivity.this, "Behandler billede", "Vent venligst", true);
        }

        @Override
        protected Void doInBackground(Bitmap... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (progress != null) {
                progress.dismiss();
            }
        }
    }

}

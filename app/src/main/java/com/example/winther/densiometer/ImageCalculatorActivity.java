package com.example.winther.densiometer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.winther.densiometer.calculations.DensioMeterCalculator;

import java.io.IOException;

public class ImageCalculatorActivity extends AppCompatActivity {
    private static final String TAG = "ImageCalculatorActivity";
    private static final int REQUEST_IMAGE_CODE = 4321;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_calculator);

        Button button = (Button) findViewById(R.id.pick_image);

        assert button != null;
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
            new ProcessImageTask().execute(mBitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private class ProcessImageTask extends AsyncTask<Bitmap, Void, Float> {
        private ProgressDialog progress;
        private Bitmap processedBitmap;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ImageCalculatorActivity.this, "Behandler billede", "Vent venligst", true);
        }

        @Override
        protected Float doInBackground(Bitmap... params) {
            float treeCoverage = 0;

            // assert only one image
            if (params.length == 1) {
                DensioMeterCalculator calculator = new DensioMeterCalculator(params[0]);

                // Get the processed bitmap for feedback
                processedBitmap = calculator.getProcessedBitmap();

                // Get the number of covered squares
                treeCoverage = calculator.getTreeCoverage();
            }

            return treeCoverage;
        }

        @Override
        protected void onPostExecute(Float numberOfCoveredSquares) {
            if (progress != null) {
                progress.dismiss();
            }

            String formattedPercentage = " ";
            formattedPercentage += Math.round((numberOfCoveredSquares*100)) + "%";

            TextView textView = (TextView) findViewById(R.id.text_image_result);
            assert textView != null;
            textView.setText(getString(R.string.tree_cover_is) + formattedPercentage );

            // Update the bitmap
            if (processedBitmap != null) {
                ImageView processedImageView = (ImageView) findViewById(R.id.processed_image_view);
                assert processedImageView != null;
                processedImageView.setImageBitmap(processedBitmap);
            }
        }
    }

}

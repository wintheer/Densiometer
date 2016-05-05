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


    private class ProcessImageTask extends AsyncTask<Bitmap, Void, Long> {
        private ProgressDialog progress;
        private Bitmap processedBitmap;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ImageCalculatorActivity.this, "Behandler billede", "Vent venligst", true);
        }

        @Override
        protected Long doInBackground(Bitmap... params) {
            long totalAvg = 0;

            // assert only one image
            if (params.length == 1) {
                DensioMeterCalculator calculator = new DensioMeterCalculator(params[0]);

                // Calculate the total average
                totalAvg = calculator.calculateTotalAvg();

                // Get the processed bitmap for feedback
                processedBitmap = calculator.getProcessedBitmap();
            }

            return totalAvg;
        }

        /**
         * Runs through the pixels array, and calculates average light value for the entire image
         * Stores intermediate results in the lightValues array.
         *
         * @param pixels      the array of pixels to run through
         * @param lightValues stores the intermediate value for every pixel here
         * @return the average lighting value for the entire image
         */
        private long calculateTotalAvg(int[] pixels, int[] lightValues) {
            int length = pixels.length;
            long totalAvg = 0;
            for (int i = 0; i < length; i++) {
                int red = (pixels[i] & 0x00FF0000) >> 16;
                int green = (pixels[i] & 0x0000FF00) >> 8;
                int blue = (pixels[i] & 0x000000FF);

                int avg = 0;
                avg += red;
                avg += green;
                avg += blue;

                avg = avg / 3;
                lightValues[i] = avg;
            }

            for (int pixel : lightValues) {
                totalAvg += pixel;
            }

            totalAvg = totalAvg / length;
            return totalAvg;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            if (progress != null) {
                progress.dismiss();
            }

            if (processedBitmap != null) {
                ImageView processedImageView = (ImageView) findViewById(R.id.processed_image_view);
                processedImageView.setImageBitmap(processedBitmap);
            }

            TextView resultTextView = (TextView) findViewById(R.id.image_textview_for_results);
            resultTextView.setText("Middelværdi for billede er: " + aLong);
        }
    }

}

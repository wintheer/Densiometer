package com.example.winther.densiometer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.winther.densiometer.calculations.DensioMeterCalculator;
import com.example.winther.densiometer.calculations.ExifUtil;
import com.example.winther.densiometer.models.Measurement;
import com.example.winther.densiometer.util.ImageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ImageCalculatorActivity extends AppCompatActivity {
    private static final String TAG = "ImageCalculatorActivity";
    private String fileNameAndPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_calculator);

        fileNameAndPath = getIntent().getStringExtra("filename");

        new ProcessImageTask().execute(fileNameAndPath);
    }

    private class ProcessImageTask extends AsyncTask<String, Void, Float> {
        private ProgressDialog progress;
        private Bitmap processedBitmap;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ImageCalculatorActivity.this, "Behandler billede", "Vent venligst", true);
        }

        @Override
        protected Float doInBackground(String... params) {
            // Get the file
            File f=new File(params[0]);
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            float treeCoverage;

            // assert only one image
            DensioMeterCalculator calculator = new DensioMeterCalculator(bitmap);

            // Get the processed bitmap for feedback
            processedBitmap = calculator.getProcessedBitmap();

            // Get the number of covered squares
            treeCoverage = calculator.getTreeCoverage();

            return treeCoverage;
        }

        @Override
        protected void onPostExecute(final Float numberOfCoveredSquares) {
            if (progress != null) {
                progress.dismiss();
            }

            String formattedPercentage = " ";
            formattedPercentage += Math.round((numberOfCoveredSquares * 100)) + "%";

            TextView textView = (TextView) findViewById(R.id.text_image_result);
            assert textView != null;
            textView.setText(getString(R.string.tree_cover_is) + formattedPercentage);

            File manipulatedFile = null;
            // Update the bitmap
            if (processedBitmap != null) {
                ImageView processedImageView = (ImageView) findViewById(R.id.processed_image_view);
                assert processedImageView != null;
                processedImageView.setImageBitmap(processedBitmap);

                manipulatedFile = ImageUtils.getOutputMediaFile();
                if (manipulatedFile == null) return;
                try {
                    FileOutputStream fos = new FileOutputStream(manipulatedFile);
                    processedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            assert manipulatedFile != null;
            final String filenameAndPath = manipulatedFile.getAbsolutePath();

            // Add onClickListener for the "Godkend" button
            Button saveButton = (Button) findViewById(R.id.save_result_for_image);
            assert saveButton != null;
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create the Realm configuration
                    RealmConfiguration realmConfig = new RealmConfiguration.Builder(ImageCalculatorActivity.this).deleteRealmIfMigrationNeeded().build();
                    // Open the Realm for the UI thread.
                    Realm realm = Realm.getInstance(realmConfig);
                    realm.beginTransaction();
                    Measurement measurement = realm.createObject(Measurement.class);
                    measurement.setMeasurement(Math.round((numberOfCoveredSquares * 100)));
                    measurement.setImagePath(fileNameAndPath);
                    measurement.setCalculatedImagePath(filenameAndPath);
                    realm.commitTransaction();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("measurement", measurement.getMeasurement());
                    setResult(20, resultIntent);
                    finish();
                    realm.close();
                }
            });
        }
    }

}

package com.example.winther.densiometer.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtils {
    /**
     * Create a File for saving an image or video
     */
    public static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DensiometerApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Class:Main", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        String filenameOfPath = mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg";
        mediaFile = new File(filenameOfPath);

        return mediaFile;
    }

    public String saveToInternalStorage(Context applicationContent, Bitmap bitmapImage) {
        ContextWrapper contextWrapper = new ContextWrapper(applicationContent);
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
}

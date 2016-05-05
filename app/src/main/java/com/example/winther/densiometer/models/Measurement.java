package com.example.winther.densiometer.models;

import android.net.Uri;

import io.realm.RealmObject;

/**
 * Represents a single measurement.
 */
public class Measurement extends RealmObject {
    /**
     * The tree coverage of this measurement
     */
    private int measurement;

    /**
     * The path to the image related to this measurement
     */
    private String imagePath;

    public int getMeasurement() {
        return measurement;
    }

    public void setMeasurement(int measurement) {
        this.measurement = measurement;
    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

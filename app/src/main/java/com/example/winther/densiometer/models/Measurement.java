package com.example.winther.densiometer.models;

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
     * The path to the original image related to this measurement
     */
    private String imagePath;

    /**
     * The path to the annotated/manipulated image of the measurement
     */
    private String calculatedImagePath;

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

    public String getCalculatedImagePath() {
        return calculatedImagePath;
    }

    public void setCalculatedImagePath(String calculatedImagePath) {
        this.calculatedImagePath = calculatedImagePath;
    }
}

package com.example.winther.densiometer.calculations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * The DensioMeterClass
 * Calculates and draws squares and more.
 */
public class DensioMeterCalculator {
    private static final String TAG = "DensioMeterCalculator";

    /**
     * The number of rows and columns on the image
     * The number of "fields" in the image is rows * columns
     */
    private static final int numberOfRows = 8;
    private static final int numberOfColumns = 8;

    /**
     * The percentage below the average a field must be to be considered "covered"
     */
    private static final float thresholdBelowAverage = 0.7f;

    private Bitmap bitmap;
    private int[] pixels;
    private int[] lightValues;
    private int height;
    private int width;
    private long totalAvg;
    private int numberOfCoveredSquares = 0;

    public DensioMeterCalculator(Bitmap bitmap) {
        this.bitmap = bitmap;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        this.lightValues = new int[pixels.length];
    }

    /**
     * Runs through the pixels array, and calculates average light value for the entire image
     * Stores intermediate results in the lightValues array.
     *
     * @return the average lighting value for the entire image
     */
    public long calculateTotalAvg() {
        if (this.totalAvg != 0) { // Load cached result
            return this.totalAvg;
        }

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

        this.totalAvg = totalAvg; // Cache result
        return totalAvg;
    }

    public int[] getRowPixelDimensions() {
        int[] rows = new int[numberOfRows - 1];
        for (int i = 1; i < numberOfRows; i++) {
            int widthOfRow = (int) ((i) / (float) numberOfRows * width);
            rows[i - 1] = widthOfRow;
        }
        return rows;
    }

    public int[] getColumnPixelDimensions() {
        int[] columns = new int[numberOfColumns - 1];
        for (int i = 1; i < numberOfColumns; i++) {
            int heightOfColumn = (int) ((i) / (float) numberOfColumns * height);
            columns[i - 1] = heightOfColumn;
        }
        return columns;
    }

    /**
     * Calculate the average RGB-value for a given image
     *
     * @param pixels array of pixels from getPixels
     * @return the average lighting value
     */
    private long calculateAverage(int[] pixels) {
        int length = pixels.length;
        long average = 0;
        for (int pixel : pixels) {
            int red = (pixel & 0x00FF0000) >> 16;
            int green = (pixel & 0x0000FF00) >> 8;
            int blue = (pixel & 0x000000FF);

            int avg = 0;
            avg += red;
            avg += green;
            avg += blue;

            avg = avg / 3;

            average += avg;
        }

        average = average / length;
        return average;
    }

    /**
     * Create the processed Bitmap based on the provided bitmap image
     *
     * @return the processed Bitmap
     */
    public Bitmap getProcessedBitmap() {
        //Bitmap processedBitmap = bitmap.copy(bitmap.getConfig(), true);
        Bitmap processedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        processedBitmap.eraseColor(Color.BLACK);
        Canvas canvas = new Canvas(processedBitmap);
        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(10);

        Paint darkSquarePaint = new Paint();
        darkSquarePaint.setAlpha(150);

        // Build the partial bitmaps
        int heightOfASingleRow = getColumnPixelDimensions()[0];
        int widthOfASingleRow = getRowPixelDimensions()[0];

        for (int i = 0; i < numberOfColumns; i++) {
            for (int j = 0; j < numberOfRows; j++) {
                Bitmap square = Bitmap.createBitmap(bitmap, i * widthOfASingleRow, j * heightOfASingleRow, widthOfASingleRow, heightOfASingleRow);
                int[] squarepixels = new int[square.getWidth() * square.getHeight()];
                square.getPixels(squarepixels, 0, square.getWidth(), 0, 0, square.getWidth(), square.getHeight());
                long avg = calculateAverage(squarepixels);

                if (avg > calculateTotalAvg() * thresholdBelowAverage) {
                    canvas.drawBitmap(square, i * widthOfASingleRow, j * heightOfASingleRow, linePaint);
                } else {
                    // Covered square
                    numberOfCoveredSquares++;
                    canvas.drawBitmap(square, i * widthOfASingleRow, j * heightOfASingleRow, darkSquarePaint);
                }
            }
        }

        // Draw vertical lines
        for (int widthOfRow : getRowPixelDimensions()) {
            canvas.drawLine(widthOfRow, 0, widthOfRow, height, linePaint);
        }

        // Draw horizontal lines
        for (int heightOfRow : getColumnPixelDimensions()) {
            // Draw a thick line
            canvas.drawLine(0, heightOfRow, width, heightOfRow, linePaint);
        }

        return processedBitmap;
    }

    // Vanilla getters and setters
    public float getTreeCoverage() {
        return (float) numberOfCoveredSquares / (numberOfColumns * numberOfRows);
    }
}

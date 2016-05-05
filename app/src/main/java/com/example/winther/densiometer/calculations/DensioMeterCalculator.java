package com.example.winther.densiometer.calculations;

/**
 * Created by Christoffer on 05-05-2016.
 */
public class DensioMeterCalculator {
    /**
     * The number of rows and columns on the image
     * The number of "fields" in the image is rows * columns
     */
    private static final int numberOfRows = 4;
    private static final int numberOfColumns = 4;

    private int[] pixels;
    private int[] lightValues;
    private int height;
    private int width;

    public DensioMeterCalculator(int[] pixels, int width, int height) {
        this.pixels = pixels;
        this.lightValues = new int[pixels.length];
        this.width = width;
        this.height = height;
    }

    /**
     * Runs through the pixels array, and calculates average light value for the entire image
     * Stores intermediate results in the lightValues array.
     * @return the average lighting value for the entire image
     */
    public long calculateTotalAvg() {
        int length = pixels.length;
        long totalAvg = 0;
        for (int i = 0; i < length; i++) {
            int red=(pixels[i] & 0x00FF0000)>>16;
            int green=(pixels[i] & 0x0000FF00)>>8;
            int blue=(pixels[i] & 0x000000FF);

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

    public int[] getRowPixelDimensions() {
        int[] rows = new int[numberOfRows-1];
        for (int i = 1;i < numberOfRows;i++) {
            int widthOfRow = (int) ((i) / (float) numberOfRows * width);
            rows[i-1] = widthOfRow;
        }
        return rows;
    }

    public int[] getColumnPixelDimensions() {
        int[] columns = new int[numberOfColumns-1];
        for (int i = 1;i < numberOfColumns;i++) {
            int heightOfColumn = (int) ((i) / (float) numberOfColumns * height);
            columns[i-1] = heightOfColumn;
        }
        return columns;
    }
}

package com.example.countries.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


public class CountryImageHelper {

    /**
     * Generates a bitmap image from a letter with specified size, background color, and text color.
     *
     * @param letter    The letter to be displayed in the image.
     * @param imageSize The size of the image (width and height).
     * @param bgColor   The background color of the image in ARGB format.
     * @param txtColor  The text color of the letter in ARGB format.
     * @return The generated bitmap image.
     */
    public static Bitmap generateImageFromLetter(char letter, int imageSize, int bgColor, int txtColor) {
        Bitmap bitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        paint.setColor(bgColor);
        canvas.drawCircle(imageSize / 2f, imageSize / 2f, imageSize / 2f, paint);

        paint.setColor(txtColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextSize(imageSize * 0.5f); // Adjust the text size based on the image size

        float xPos = (canvas.getWidth() - paint.measureText(String.valueOf(letter))) / 2;
        float yPos = (canvas.getHeight() - paint.ascent() - paint.descent()) / 2;

        canvas.drawText(String.valueOf(letter), xPos, yPos, paint);
        return bitmap;
    }

    /**
     * Calculates the contrasting color based on the given color.
     *
     * @param color The color in ARGB format.
     * @return The contrasting color (either Color.WHITE or Color.BLACK).
     */
    public static int getContrastingColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int brightness = (int) Math.sqrt(red * red * 0.299 + green * green * 0.587 + blue * blue * 0.114);
        int threshold = 150;
        return brightness < threshold ? Color.WHITE : Color.BLACK;
    }
}

package com.example.countries.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class CountryImageHelper {
    public static Bitmap generateImageFromLetter(char letter, int imageSize, int bgColor, int txtColor) {
        Bitmap bitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        Random random = new Random();

        // int backgroundColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));

        @SuppressWarnings("UnnecessaryLocalVariable")
        int backgroundColor = bgColor;


        paint.setColor(backgroundColor);
        canvas.drawCircle(imageSize / 2f, imageSize / 2f, imageSize / 2f, paint);

        // int textColor = getContrastingColor(backgroundColor);
        @SuppressWarnings("UnnecessaryLocalVariable")
        int textColor = txtColor;
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextSize(imageSize * 0.5f); // Adjust the text size based on the image size

        float xPos = (canvas.getWidth() - paint.measureText(String.valueOf(letter))) / 2;
        float yPos = (canvas.getHeight() - paint.ascent() - paint.descent()) / 2;

        canvas.drawText(String.valueOf(letter), xPos, yPos, paint);
        return bitmap;
    }

    public static int getContrastingColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int brightness = (int) Math.sqrt(red * red * 0.299 + green * green * 0.587 + blue * blue * 0.114);
        int threshold = 150;
        return brightness < threshold ? Color.WHITE : Color.BLACK;
    }
}

package com.example.countries.entity;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "countries")
public class Country {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(unique = true, canBeNull = false)
    private String name;

    @DatabaseField
    private String capital;

    @DatabaseField
    private int backgroundColor;

    @DatabaseField
    private int textColor;

    private Bitmap bitmap;

    public Country() {
    }

    /**
     * Retrieves the background color of the country.
     *
     * @return The background color in ARGB format.
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the background color of the country.
     *
     * @param backgroundColor The background color to set in ARGB format.
     */
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Retrieves the text color of the country.
     *
     * @return The text color in ARGB format.
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * Sets the text color of the country.
     *
     * @param textColor The text color to set in ARGB format.
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    /**
     * Retrieves the bitmap image associated with the country.
     *
     * @return The bitmap image.
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Sets the bitmap image for the country.
     *
     * @param bitmap The bitmap image to set.
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * Retrieves the ID of the country.
     *
     * @return The ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the country.
     *
     * @param id The ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the name of the country.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the country.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the capital of the country.
     *
     * @return The capital.
     */
    public String getCapital() {
        return capital;
    }

    /**
     * Sets the capital of the country.
     *
     * @param capital The capital to set.
     */
    public void setCapital(String capital) {
        this.capital = capital;
    }

    @Override
    @Nullable
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capital='" + capital + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", textColor='" + textColor + '\'' +
                ", bitmap=" + bitmap +
                '}';
    }
}

package com.example.countries.entity;

import android.graphics.Bitmap;

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

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @DatabaseField
    private int textColor;
    private Bitmap bitmap;

    public Country() {
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    @Override
    public String toString() {
        return "Country{" + "id=" + id + ", name='" + name + '\'' + ", capital='" + capital + '\'' + ", backgroundColor='" + backgroundColor + '\'' + ", textColor='" + textColor + '\'' + ", bitmap=" + bitmap + '}';
    }
}

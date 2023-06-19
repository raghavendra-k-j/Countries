package com.example.countries.database;

import android.graphics.Color;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.countries.Helper;
import com.example.countries.SampleDataHelper;
import com.example.countries.activities.MainActivity;
import com.example.countries.entity.Country;
import com.example.countries.helpers.CountryImageHelper;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("UnusedReturnValue")
public class DatabaseMethods {
    public final String TAG = Helper.tag(this);
    private final DatabaseHelper databaseHelper;

    public DatabaseMethods(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Escapes single quotes in the given input string for ormlite database operations.
     *
     * @param input The input string that may contain single quotes.
     * @return The input string with single quotes escaped by doubling them (' -> '').
     */
    private String escapeQuotes(String input) {
        return input.replace("'", "''");
    }

    /**
     * Clears the countries table by deleting all rows from the "countries" table in the database.
     * This method uses a raw query to perform the delete operation.
     * Any exceptions encountered during the operation will be logged, and a RuntimeException will be thrown.
     */
    public void clearCountriesTable() {
        try {
            GenericRawResults<String[]> rawResults = databaseHelper.getCountryDao().queryRaw("DELETE FROM countries");
            rawResults.close();
            Log.d(TAG, "clearCountriesTable: Completed");
        } catch (Exception e) {
            Log.d(TAG, "clearCountriesTable: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts sample data into the database using the provided SampleDataHelper.
     * If clearPresentData is set to true, the existing data in the "countries" table will be cleared before inserting the new data.
     * The method retrieves a list of countries from the SampleDataHelper and iterates over each country.
     * If a country does not already exist in the database, it is added using the addCountry method.
     *
     * @param sampleDataHelper The SampleDataHelper object containing the sample data.
     * @param clearPresentData A flag indicating whether to clear the existing data in the "countries" table.
     */
    public void insertSampleDataToDatabase(SampleDataHelper sampleDataHelper, boolean clearPresentData) {
        if (clearPresentData) {
            clearCountriesTable();
        }

        ArrayList<Country> countryArrayList = sampleDataHelper.getCountriesArrayList();
        ArrayList<Country> countriesToAdd = new ArrayList<>();

        for (Country country : countryArrayList) {
            if (!isCountryExists(country.getName())) {
                countriesToAdd.add(country);
            }
        }

        if (!countriesToAdd.isEmpty()) {
            for (Country country : countriesToAdd) {
                addCountry(country);
            }
        }
    }

    /**
     * Retrieves a list of countries from the database based on the specified page number.
     *
     * @param pageNumber           The page number of the desired data.
     * @param searchForCountryName Country name to be searched
     * @return An ArrayList containing the countries for the specified page.
     * @throws RuntimeException if a SQLException occurs during the query.
     */
    public ArrayList<Country> getCountries(int pageNumber, String searchForCountryName) {
        try {
            QueryBuilder<Country, Integer> queryBuilder = databaseHelper.getCountryDao().queryBuilder();
            queryBuilder.limit((long) MainActivity.PAGE_SIZE).offset((long) (pageNumber - 1) * MainActivity.PAGE_SIZE);
            if(searchForCountryName != null) {
                queryBuilder.where().like("name", "%" + searchForCountryName + "%");
                queryBuilder.orderByRaw("CASE WHEN name LIKE '" + searchForCountryName + "%' THEN 0 ELSE 1 END");
            }
            else {
                queryBuilder.orderBy("name", true);
            }
            ArrayList<Country> countryArrayList = new ArrayList<>(queryBuilder.query());
            int countryArrayListSize = countryArrayList.size();
            for (int i = 0; i < countryArrayListSize; i++) {
                Country country = countryArrayList.get(i);
                countryArrayList.get(i).setBitmap(CountryImageHelper.generateImageFromLetter(country.getName().charAt(0), 70, country.getBackgroundColor(), country.getTextColor()));
            }
            return countryArrayList;
        } catch (SQLException e) {
            Log.d(TAG, "sql exception: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a country with the given name exists in the database.
     *
     * @param countryName The name of the country to check.
     * @return True if the country exists, false otherwise.
     */
    public boolean isCountryExists(String countryName) {
        try {
            QueryBuilder<Country, Integer> queryBuilder = databaseHelper.getCountryDao().queryBuilder();
            queryBuilder.where().eq("name", escapeQuotes(countryName));
            return queryBuilder.countOf() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the given country name is available for update, excluding the country with the given ID.
     *
     * @param id   The ID of the country to exclude from the check.
     * @param name The new name to check availability for.
     * @return True if the country name is available for update, false otherwise.
     */
    public boolean isCountryNameAvailableForUpdate(int id, String name) {
        try {
            QueryBuilder<Country, Integer> queryBuilder = databaseHelper.getCountryDao().queryBuilder();
            queryBuilder.where().eq("name", escapeQuotes(name)).and().ne("id", id);
            return queryBuilder.countOf() == 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a new country to the database.
     *
     * @param country The country object to be added.
     * @return True if the country was successfully added, false otherwise.
     */
    public boolean addCountry(Country country) {
        Random random = new Random();
        int backgroundColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        int textColor = CountryImageHelper.getContrastingColor(backgroundColor);
        country.setBackgroundColor(backgroundColor);
        country.setTextColor(textColor);
        int rowsAffected = databaseHelper.getCountryDao().create(country);
        return rowsAffected > 0;
    }

    /**
     * Updates an existing country in the database.
     * @param country The country object to be updated.
     * @return True if the country was successfully updated, false otherwise.
     */
    public boolean updateCountry(Country country) {
        try {
            UpdateBuilder<Country, Integer> updateBuilder = databaseHelper.getCountryDao().updateBuilder();
            updateBuilder.updateColumnValue("name", country.getName());
            updateBuilder.updateColumnValue("capital", country.getCapital());
            updateBuilder.where().eq("id", country.getId());
            int rowsAffected = updateBuilder.update();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a country from the database.
     * @param country The country object to be deleted.
     * @return True if the country was successfully deleted, false otherwise.
     */
    public boolean deleteCountry(Country country) {
        try {
            DeleteBuilder<Country, Integer> deleteBuilder = databaseHelper.getCountryDao().deleteBuilder();
            deleteBuilder.where().eq("id", country.getId());
            int rowsAffected = deleteBuilder.delete();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a country from the database based on the given ID.
     *
     * @param id The ID of the country to retrieve.
     * @return The Country object if found, or null if not found or an error occurs.
     */
    @Nullable
    public Country getCountry(int id) {
        try {
            QueryBuilder<Country, Integer> queryBuilder = databaseHelper.getCountryDao().queryBuilder();
            queryBuilder.where().eq("id", id);
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            Log.d(TAG, "getCountry: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves the total number of countries in the database.
     *
     * @return The total number of countries.
     */
    public int getTotalCountries(String searchForCountryName) {
        try {
            QueryBuilder<Country, Integer> queryBuilder = databaseHelper.getCountryDao().queryBuilder();
            if(searchForCountryName != null) {
                queryBuilder.where().like("name", "%" + searchForCountryName + "%");
            }
            return (int) queryBuilder.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Calculates the total number of pages based on the total number of countries and the page size.
     *
     * @param totalCountries The total number of countries.
     * @return The total number of pages.
     */
    public int getTotalPages(long totalCountries) {
        return (int) Math.ceil((double) totalCountries / MainActivity.PAGE_SIZE);
    }

}

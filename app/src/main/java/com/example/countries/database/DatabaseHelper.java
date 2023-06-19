package com.example.countries.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.countries.Helper;
import com.example.countries.entity.Country;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "app_database";
    private static final int DB_VERSION = 2;
    private final String TAG = Helper.tag(this);
    private RuntimeExceptionDao<Country, Integer> countryDao;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Country.class);
        } catch (SQLException e) {
            Log.d(TAG, "onCreate: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Country.class, false);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.d(TAG, "onUpgrade: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the RuntimeExceptionDao for the Country entity.
     * If the dao instance is not yet initialized, it creates a new instance using getRuntimeExceptionDao().
     *
     * @return The RuntimeExceptionDao instance for the Country entity.
     */
    public RuntimeExceptionDao<Country, Integer> getCountryDao() {
        if (countryDao == null) {
            countryDao = getRuntimeExceptionDao(Country.class);
        }
        return countryDao;
    }

    @Override
    public void close() {
        countryDao = null;
        super.close();
    }
}

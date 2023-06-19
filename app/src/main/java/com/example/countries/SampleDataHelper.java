package com.example.countries;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.countries.entity.Country;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SampleDataHelper {
    private final String TAG = Helper.tag(this);
    Context context;

    public SampleDataHelper(Context context) {
        this.context = context;
    }

    private void generateRawToValidJsonFileAndSaveItInCacheDir() {
        try {
            Log.d(TAG, "generateRawToValidJsonFile: " + "Writing countries.json file to assets folder");
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("countries.json");

            int size = inputStream.available();
            byte[] buffer = new byte[size];
            //noinspection ResultOfMethodCallIgnored
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(json);
            JSONArray newJsonArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonCountryObj = jsonArray.getJSONObject(i);

                JSONObject newJsonCountryObj = new JSONObject();
                newJsonCountryObj.put("name", jsonCountryObj.getString("name"));
                newJsonCountryObj.put("capital", jsonCountryObj.getString("capital"));
                newJsonCountryObj.put("code", jsonCountryObj.getString("iso3"));

                JSONArray newStatesJsonArray = new JSONArray();
                JSONArray statesJsonArray = jsonCountryObj.getJSONArray("states");
                for (int j = 0; j < statesJsonArray.length(); j++) {
                    JSONObject newStateJsonObject = new JSONObject();
                    newStateJsonObject.put("name", statesJsonArray.getJSONObject(j).getString("name"));
                    newStateJsonObject.put("code", statesJsonArray.getJSONObject(j).getString("state_code"));
                    newStatesJsonArray.put(newStateJsonObject);
                }
                newJsonCountryObj.put("states", newStatesJsonArray);
                newJsonArray.put(newJsonCountryObj);
            }

            String newJson = newJsonArray.toString();

            try {
                File cacheDir = context.getApplicationContext().getCacheDir();
                File file = new File(cacheDir, "countries.json");
                FileWriter writer = new FileWriter(file);
                writer.write(newJson);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "generateRawToValidJsonFile: Failed: " + e.getMessage());
            }

        } catch (IOException | JSONException e) {
            Log.d(TAG, "generateRawToValidJsonFile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String readFileFromCacheDirectory() {
        String fileContent = "";
        try {
            File cacheDir = context.getApplicationContext().getCacheDir();
            File file = new File(cacheDir, "countries.json");

            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            fis.close();

            fileContent = content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "readFileFromCacheDirectory: " + e.getMessage());
        }
        return fileContent;
    }

    public ArrayList<Country> getCountriesArrayList() {
        generateRawToValidJsonFileAndSaveItInCacheDir();
        String raw = readFileFromCacheDirectory();
        ArrayList<Country> countryList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(raw);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.optString("name");
                String capital = jsonObject.optString("capital");
                Country country = new Country();
                country.setName(name);
                country.setCapital(capital);
                countryList.add(country);
            }
        } catch (Exception e) {
            Log.d(TAG, "getCountriesArrayList: " + e.getMessage());
            e.printStackTrace();
        }
        return countryList;
    }

}

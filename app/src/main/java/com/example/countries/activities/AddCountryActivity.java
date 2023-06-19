package com.example.countries.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.countries.database.DatabaseHelper;
import com.example.countries.database.DatabaseMethods;
import com.example.countries.databinding.ActivityAddCountryBinding;
import com.example.countries.entity.Country;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class AddCountryActivity extends AppCompatActivity {

    ActivityAddCountryBinding binding;
    DatabaseMethods databaseMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflating the layout using the generated ViewBinding class
        binding = ActivityAddCountryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setting up the toolbar navigation button to handle back navigation
        binding.activityAddCountryToolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Setting up the save button click listener
        binding.activityAddCountryBtnSave.setOnClickListener(v -> saveCountry());

        // Initializing the DatabaseMethods instance using the DatabaseHelper
        databaseMethods = new DatabaseMethods(OpenHelperManager.getHelper(this, DatabaseHelper.class));
    }

    @SuppressWarnings("ConstantConditions")
    private void saveCountry() {
        // Extracting the entered country name and capital from the input fields
        String name = binding.activityAddCountryName.getText().toString();
        String capital = binding.activityAddCountryCapital.getText().toString();

        // Validating that the country name is not empty
        if (name.isEmpty()) {
            binding.activityAddCountryName.setError("Name is Required");
            return;
        }

        // Validating that the capital is not empty
        if (capital.isEmpty()) {
            binding.activityAddCountryCapital.setError("Capital is Required");
            return;
        }

        // Checking if the country already exists in the database
        if (databaseMethods.isCountryExists(name)) {
            binding.activityAddCountryName.setError("Country Already Exists");
            return;
        }

        // Creating a new Country object with the entered name and capital
        Country country = new Country();
        country.setName(name);
        country.setCapital(capital);

        // Adding the country to the database
        databaseMethods.addCountry(country);

        // Creating an intent to return the ID of the added country to the calling activity
        Intent intent = new Intent();
        intent.putExtra("added_country_id", country.getId());

        // Setting the result of the activity as RESULT_OK and passing the intent
        setResult(RESULT_OK, intent);

        // Finishing the activity and returning to the calling activity
        finish();
    }
}
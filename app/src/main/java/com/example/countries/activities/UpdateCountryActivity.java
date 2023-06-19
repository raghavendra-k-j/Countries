package com.example.countries.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.countries.Helper;
import com.example.countries.database.DatabaseHelper;
import com.example.countries.database.DatabaseMethods;
import com.example.countries.databinding.ActivityUpdateCountryBinding;
import com.example.countries.entity.Country;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class UpdateCountryActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private final String TAG = Helper.tag(this);
    ActivityUpdateCountryBinding binding;
    DatabaseMethods databaseMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateCountryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*
         * Retrieve the country ID passed from the previous activity.
         * If the ID is not available, display a toast message and finish the activity.
         */
        Intent intent = getIntent();
        int countryId = intent.getIntExtra("country_id", -1);
        if (countryId == -1) {
            Toast.makeText(this, "Country not available to edit", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initialize the databaseMethods object using the DatabaseHelper.
        databaseMethods = new DatabaseMethods(OpenHelperManager.getHelper(this, DatabaseHelper.class));

        /*
         * Get the country object from the database based on the country ID.
         * If the country is not found, display a toast message and finish the activity.
         */
        Country country = databaseMethods.getCountry(countryId);
        if (country == null) {
            Toast.makeText(this, "Country not available to edit", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set the country name and capital in the corresponding EditText fields.
        binding.activityUpdateCountryName.setText(country.getName());
        binding.activityUpdateCountryCapital.setText(country.getCapital());

        // Set the navigation click listener for the toolbar.
        binding.activityUpdateCountryToolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Set the click listener for the save button.
        binding.activityUpdateCountryBtnSave.setOnClickListener(v -> saveCountry(country.getId()));
    }

    @SuppressWarnings("ConstantConditions")
    private void saveCountry(int countryId) {
        // Get the name and capital from the EditText fields.
        String name = binding.activityUpdateCountryName.getText().toString();
        String capital = binding.activityUpdateCountryCapital.getText().toString();

        // Validate if the name field is empty and show an error if required.
        if (name.isEmpty()) {
            binding.activityUpdateCountryName.setError("Name is Required");
            return;
        }

        // Validate if the capital field is empty and show an error if required.
        if (capital.isEmpty()) {
            binding.activityUpdateCountryCapital.setError("Capital is Required");
            return;
        }

        /*
         * Check if the updated country name already exists in the database
         * (excluding the current country being updated).
         * If it exists, show an error and return.
         */
        if (!databaseMethods.isCountryNameAvailableForUpdate(countryId, name)) {
            binding.activityUpdateCountryName.setError("Country name already exists");
            return;
        }

        // Create a new Country object with the updated values.
        Country updatedCountry = new Country();
        updatedCountry.setId(countryId);
        updatedCountry.setName(name);
        updatedCountry.setCapital(capital);

        // Update the country in the database.
        databaseMethods.updateCountry(updatedCountry);

        // Set the result intent and finish the activity.
        Intent intent = new Intent();
        intent.putExtra("updated_country_id", updatedCountry.getId());
        setResult(RESULT_OK, intent);
        finish();
    }
}

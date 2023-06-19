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

        Intent intent = getIntent();
        int countryId = intent.getIntExtra("country_id", -1);
        if (countryId == -1) {
            Toast.makeText(this, "Country not available to edit", Toast.LENGTH_SHORT).show();
            finish();
        }

        databaseMethods = new DatabaseMethods(OpenHelperManager.getHelper(this, DatabaseHelper.class));
        Country country = databaseMethods.getCountry(countryId);
        if (country == null) {
            Toast.makeText(this, "Country not available to edit", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.activityUpdateCountryName.setText(country.getName());
        binding.activityUpdateCountryCapital.setText(country.getCapital());

        binding.activityUpdateCountryToolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.activityUpdateCountryBtnSave.setOnClickListener(v -> saveCountry(country.getId()));
    }

    @SuppressWarnings("ConstantConditions")
    private void saveCountry(int countryId) {
        String name = binding.activityUpdateCountryName.getText().toString();
        String capital = binding.activityUpdateCountryCapital.getText().toString();

        if (name.isEmpty()) {
            binding.activityUpdateCountryName.setError("Name is Required");
            return;
        }

        if (capital.isEmpty()) {
            binding.activityUpdateCountryCapital.setError("Capital is Required");
            return;
        }

        if (!databaseMethods.isCountryNameAvailableForUpdate(countryId, name)) {
            binding.activityUpdateCountryName.setError("Country name already exists");
            return;
        }

        Country updatedCountry = new Country();
        updatedCountry.setId(countryId);
        updatedCountry.setName(name);
        updatedCountry.setCapital(capital);
        databaseMethods.updateCountry(updatedCountry);

        Intent intent = new Intent();
        intent.putExtra("updated_country_id", updatedCountry.getId());
        setResult(RESULT_OK, intent);
        finish();
    }
}

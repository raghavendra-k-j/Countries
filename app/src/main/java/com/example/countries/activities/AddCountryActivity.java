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
        binding = ActivityAddCountryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.activityAddCountryToolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.activityAddCountryBtnSave.setOnClickListener(v -> saveCountry());
        databaseMethods = new DatabaseMethods(OpenHelperManager.getHelper(this, DatabaseHelper.class));
    }

    @SuppressWarnings("ConstantConditions")
    private void saveCountry() {
        String name = binding.activityAddCountryName.getText().toString();
        String capital = binding.activityAddCountryCapital.getText().toString();

        if (name.isEmpty()) {
            binding.activityAddCountryName.setError("Name is Required");
            return;
        }

        if (capital.isEmpty()) {
            binding.activityAddCountryCapital.setError("Capital is Required");
            return;
        }

        if (databaseMethods.isCountryExists(name)) {
            binding.activityAddCountryName.setError("Country Already Exists");
            return;
        }

        Country country = new Country();
        country.setName(name);
        country.setCapital(capital);
        databaseMethods.addCountry(country);

        Intent intent = new Intent();
        intent.putExtra("added_country_id", country.getId());
        setResult(RESULT_OK, intent);
        finish();
    }
}

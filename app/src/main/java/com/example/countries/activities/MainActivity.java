package com.example.countries.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.countries.Helper;
import com.example.countries.R;
import com.example.countries.SampleDataHelper;
import com.example.countries.adapter.CountryAdapter;
import com.example.countries.database.DatabaseHelper;
import com.example.countries.database.DatabaseMethods;
import com.example.countries.databinding.ActivityMainBinding;
import com.example.countries.entity.Country;
import com.example.countries.interfaces.MainActivityInterface;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity implements MainActivityInterface {

    public static final int ADD_COUNTRY_REQ_CODE = 1;
    public static final int UPDATE_COUNTRY_REQ_CODE = 2;
    public static final int PAGE_SIZE = 10;
    @SuppressWarnings("unused")
    private final String TAG = Helper.tag(this);
    private ActivityMainBinding binding;
    private DatabaseMethods databaseMethods;
    private CountryAdapter countryAdapter;
    private ArrayList<Country> countries;
    private SampleDataHelper sampleDataHelper;
    private ExecutorService executor;
    private int currentPage = 1;
    private int totalPages;
    int totalCountries;

    public void setCountries(ArrayList<Country> countryArrayList) {
        this.countries.clear();
        this.countries.addAll(countryArrayList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.mainToolbar);

        countries = new ArrayList<>();
        sampleDataHelper = new SampleDataHelper(this);
        databaseMethods = new DatabaseMethods(OpenHelperManager.getHelper(this, DatabaseHelper.class));
        countryAdapter = new CountryAdapter(this, countries);
        executor = Executors.newSingleThreadExecutor();

        binding.mainToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_on_surface));
        binding.mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.mainRecyclerView.setAdapter(countryAdapter);


        binding.mainBtnPrev.setOnClickListener(v -> loadList(currentPage - 1));
        binding.mainBtnNext.setOnClickListener(v -> loadList(currentPage + 1));

        binding.mainBtnPrev.setOnLongClickListener(v -> {
            loadList(1);
            return true;
        });

        binding.mainBtnNext.setOnLongClickListener(v -> {
            loadList(totalPages);
            return true;
        });

        binding.mainEditTextSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchTerm = textView.getText().toString().trim();
                loadList(1);
                return true;
            }
            return false;
        });

        loadList(currentPage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_toolbar_menu_item_search) {
            if(binding.mainEditTextSearch.getVisibility() == View.VISIBLE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                if (view != null) {
                    view.clearFocus();
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                binding.mainEditTextSearch.setText("");
                binding.mainEditTextSearch.setVisibility(View.GONE);
                item.setIcon(R.drawable.ic_search_on_surface);
                countryAdapter.setSearchQuery(null);
                loadList(1);
            }
            else {
                binding.mainEditTextSearch.setVisibility(View.VISIBLE);
                binding.mainEditTextSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                if (view != null) {
                    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                }
                item.setIcon(R.drawable.ic_close);
            }
            return true;
        } else if (id == R.id.main_toolbar_menu_item_add) {
            onAddCountriesButtonClicked();
            return true;
        } else if (id == R.id.main_toolbar_menu_item_add_sample_data) {
            addSampleData();
        } else if (id == R.id.main_toolbar_menu_item_clean_data) {
            cleanData();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    private void addSampleData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding Sample Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        executor.submit(() -> {
            databaseMethods.insertSampleDataToDatabase(sampleDataHelper, true);
            runOnUiThread(() -> {
                progressDialog.dismiss();
                loadList(1);
            });
        });
    }

    @SuppressWarnings("deprecation")
    private void cleanData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cleaning Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        executor.submit(() -> {
            databaseMethods.clearCountriesTable();
            runOnUiThread(() -> {
                progressDialog.dismiss();
                loadList(1);
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) executor.shutdown();
    }

    private void onAddCountriesButtonClicked() {
        Intent intent = new Intent(this, AddCountryActivity.class);
        //noinspection deprecation
        startActivityForResult(intent, ADD_COUNTRY_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_COUNTRY_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                int addedCountryId = data.getIntExtra("added_country_id", -1);
                Country addedCountry = databaseMethods.getCountry(addedCountryId);
                if (addedCountry != null) {
                    loadList(1);
                }
            }
        } else if (requestCode == UPDATE_COUNTRY_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                int updatedCountryId = data.getIntExtra("updated_country_id", -1);
                Country updatedCountry = databaseMethods.getCountry(updatedCountryId);
                if (updatedCountry != null) {
                    int currentPageRef =  currentPage;
                    loadList(1);
                    if(currentPageRef > totalPages) {
                        loadList(totalPages);
                    }
                    else {
                        loadList(currentPageRef);
                    }
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void loadList(int pageNumber) {
        Log.d(TAG, "loadList: Requesting to load" + pageNumber);
        String searchForCountryName = null;
        if(binding.mainEditTextSearch.getVisibility() == View.VISIBLE) {
            //noinspection ConstantConditions
            searchForCountryName = binding.mainEditTextSearch.getText().toString().trim();
            if(searchForCountryName.isEmpty()) {
                searchForCountryName = null;
                countryAdapter.setSearchQuery(null);
            }
            else {
                countryAdapter.setSearchQuery(searchForCountryName);
            }
        }
        totalCountries = databaseMethods.getTotalCountries(searchForCountryName);
        totalPages = databaseMethods.getTotalPages(totalCountries);
        if (totalPages == 0) {
            Log.d(TAG, "loadList: No Pages Found" + totalPages + " Pages");
            setCountries(new ArrayList<>());
            countryAdapter.setCountries(countries);
            currentPage = 1;
            binding.mainPaginationContainer.setVisibility(View.GONE);
            return;
        }
        Log.d(TAG, "loadList: Total Pages: " + totalPages);

        if (pageNumber < 1) {
            Toast.makeText(this, "You are trying to load the 0th page", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pageNumber > totalPages) {
            Toast.makeText(this, "You are trying to load the page does not exists", Toast.LENGTH_SHORT).show();
            return;
        }

        currentPage = pageNumber;
        countries = databaseMethods.getCountries(pageNumber, searchForCountryName);
        countryAdapter.setCountries(countries);
        //noinspection ConstantConditions
        binding.mainRecyclerView.getLayoutManager().scrollToPosition(0);

        if (totalPages <= 1) {
            binding.mainPaginationContainer.setVisibility(View.GONE);
        } else {
            binding.mainPaginationContainer.setVisibility(View.VISIBLE);
            binding.mainTextViewPageInfo.setText(String.format("Page\n%02d/%02d", currentPage, totalPages));
            binding.mainTextViewTotalItems.setText(String.format("Showing\n%02d/%02d", countries.size(), totalCountries));
        }

        binding.mainBtnNext.setEnabled(currentPage != totalPages);
        binding.mainBtnPrev.setEnabled(currentPage != 1);
    }

    @Override
    public void onEditCountryButtonClicked(Country country) {
        Intent intent = new Intent(this, UpdateCountryActivity.class);
        intent.putExtra("country_id", country.getId());
        //noinspection deprecation
        startActivityForResult(intent, UPDATE_COUNTRY_REQ_CODE);
    }

    @Override
    public void onDeleteCountryButtonClicked(Country country, int position) {
        databaseMethods.deleteCountry(country);
        if(currentPage == totalPages && countries.size() == 1) {
            if(totalPages > 1) {
                loadList(totalPages -1);
            }
            else {
                loadList(1);
            }
        }
        else {
            loadList(currentPage);
        }
    }
}
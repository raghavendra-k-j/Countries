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
            // Check if the result is successful
            if (resultCode == RESULT_OK) {
                assert data != null;
                // Get the ID of the added country from the intent data
                int addedCountryId = data.getIntExtra("added_country_id", -1);
                // Retrieve the added country from the database using the ID
                Country addedCountry = databaseMethods.getCountry(addedCountryId);
                if (addedCountry != null) {
                    // Reload the list with the added country on the first page
                    loadList(1);
                }
            }
        } else if (requestCode == UPDATE_COUNTRY_REQ_CODE) {
            // Check if the result is successful
            if (resultCode == RESULT_OK) {
                assert data != null;
                // Get the ID of the updated country from the intent data
                int updatedCountryId = data.getIntExtra("updated_country_id", -1);
                // Retrieve the updated country from the database using the ID
                Country updatedCountry = databaseMethods.getCountry(updatedCountryId);
                if (updatedCountry != null) {
                    int currentPageRef = currentPage;
                    // Reload the list with the updated country on the first page
                    loadList(1);
                    if (currentPageRef > totalPages) {
                        // If the previous current page is greater than the total pages, load the last page
                        loadList(totalPages);
                    } else {
                        // Otherwise, load the previous current page
                        loadList(currentPageRef);
                    }
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void loadList(int pageNumber) {
        // Log the requested page number
        Log.d(TAG, "loadList: Requesting to load" + pageNumber);

        // Initialize the search query variable
        String searchForCountryName = null;

        // Check if the search EditText is visible
        if(binding.mainEditTextSearch.getVisibility() == View.VISIBLE) {
            // Get the search query text and trim any leading/trailing spaces
            searchForCountryName = binding.mainEditTextSearch.getText().toString().trim();

            // Check if the search query is empty
            if(searchForCountryName.isEmpty()) {
                // If empty, set the search query to null and clear the adapter's search query
                searchForCountryName = null;
                countryAdapter.setSearchQuery(null);
            }
            else {
                // If not empty, set the search query in the adapter
                countryAdapter.setSearchQuery(searchForCountryName);
            }
        }

        // Get the total number of countries based on the search query
        totalCountries = databaseMethods.getTotalCountries(searchForCountryName);

        // Calculate the total number of pages based on the total countries
        totalPages = databaseMethods.getTotalPages(totalCountries);

        // Check if there are no pages
        if (totalPages == 0) {
            // Log the absence of pages
            Log.d(TAG, "loadList: No Pages Found" + totalPages + " Pages");

            // Clear the countries list and set it in the adapter
            setCountries(new ArrayList<>());
            countryAdapter.setCountries(countries);

            // Reset the current page and hide the pagination container
            currentPage = 1;
            binding.mainPaginationContainer.setVisibility(View.GONE);
            return;
        }

        // Log the total number of pages
        Log.d(TAG, "loadList: Total Pages: " + totalPages);

        // Check if the requested page number is less than 1
        if (pageNumber < 1) {
            // Show a toast message indicating an invalid page number
            Toast.makeText(this, "You are trying to load the 0th page", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the requested page number is greater than the total number of pages
        if (pageNumber > totalPages) {
            // Show a toast message indicating a non-existent page number
            Toast.makeText(this, "You are trying to load the page does not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set the current page to the requested page number
        currentPage = pageNumber;

        // Retrieve the list of countries for the current page and search query
        countries = databaseMethods.getCountries(pageNumber, searchForCountryName);

        // Set the retrieved countries list in the adapter
        countryAdapter.setCountries(countries);

        // Scroll the RecyclerView to the top position
        //noinspection ConstantConditions
        binding.mainRecyclerView.getLayoutManager().scrollToPosition(0);

        // Check the total number of pages to determine the visibility of the pagination container
        if (totalPages <= 1) {
            binding.mainPaginationContainer.setVisibility(View.GONE);
        } else {
            binding.mainPaginationContainer.setVisibility(View.VISIBLE);

            // Set the page info text with the current page number and total pages
            binding.mainTextViewPageInfo.setText(String.format("Page\n%02d/%02d", currentPage, totalPages));

            // Set the total items text with the number of countries shown and the total number of countries
            binding.mainTextViewTotalItems.setText(String.format("Showing\n%02d/%02d", countries.size(), totalCountries));
        }

        // Enable/disable the next and previous buttons based on the current page number
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
        // Delete the country from the database
        databaseMethods.deleteCountry(country);

        // Check if the current page is the last page and there is only one country left on that page
        if(currentPage == totalPages && countries.size() == 1) {
            // Check if there are more than one page in total
            if(totalPages > 1) {
                // Load the previous page (current page - 1)
                loadList(totalPages - 1);
            }
            else {
                // Load the first page
                loadList(1);
            }
        }
        else {
            // Reload the current page
            loadList(currentPage);
        }
    }

}
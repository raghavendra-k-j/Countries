package com.example.countries.interfaces;

import com.example.countries.entity.Country;

public interface MainActivityInterface {

    /**
     * Callback method invoked when the "Edit" button is clicked for a country.
     *
     * @param country The country object associated with the clicked button.
     */
    void onEditCountryButtonClicked(Country country);

    /**
     * Callback method invoked when the "Delete" button is clicked for a country.
     *
     * @param country The country object associated with the clicked button.
     * @param pos     The position of the country in the list.
     */
    void onDeleteCountryButtonClicked(Country country, int pos);
}

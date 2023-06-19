package com.example.countries.interfaces;

import com.example.countries.entity.Country;

public interface MainActivityInterface {
    void onEditCountryButtonClicked(Country country);

    void onDeleteCountryButtonClicked(Country country, int pos);
}

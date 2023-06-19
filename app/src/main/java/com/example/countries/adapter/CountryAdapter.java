package com.example.countries.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countries.R;
import com.example.countries.databinding.ListItemCountryBinding;
import com.example.countries.entity.Country;
import com.example.countries.interfaces.MainActivityInterface;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {

    private final Context context;
    private final ArrayList<Country> countryArrayList;

    private String searchQuery;

    private boolean isSearchQueryIsNotEmpty = false;
    private final LayoutInflater inflater;

    private final MainActivityInterface mainActivityInterface;

    public CountryAdapter(Context context, ArrayList<Country> countryArrayList) {
        this.context = context;
        this.countryArrayList = countryArrayList;
        inflater = LayoutInflater.from(context);
        mainActivityInterface = (MainActivityInterface) context;
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CountryViewHolder(ListItemCountryBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        Country country = countryArrayList.get(position);
        holder.binding.listItemCountryTextviewName.setText(country.getName());
        holder.binding.listItemCountryTextviewCapital.setText(country.getCapital());
        holder.binding.listItemCountryImageViewRight.setOnClickListener(v -> showPopupMenu(v, country, position));
        holder.binding.listItemCountryImage.setImageBitmap(country.getBitmap());
        highlightSearchText(holder.binding.listItemCountryTextviewName, country.getName());
    }

    @Override
    public int getItemCount() {
        return countryArrayList.size();
    }

    private void showPopupMenu(View view, Country country, int pos) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_country_item, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_country_item_menu_item_edit) {
                mainActivityInterface.onEditCountryButtonClicked(country);
                return true;
            } else if (itemId == R.id.menu_country_item_menu_item_delete) {
                mainActivityInterface.onDeleteCountryButtonClicked(country, pos);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCountries(ArrayList<Country> countries) {
        this.countryArrayList.clear();
        this.countryArrayList.addAll(countries);
        notifyDataSetChanged();
    }

    public void addItemToTop(Country country, int position) {
        countryArrayList.add(position, country);
        notifyItemInserted(position);
    }

    public void updateItem(Country country, int position) {
        countryArrayList.set(position, country);
        notifyItemChanged(position);
    }

    public void deleteItem(int position) {
        countryArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public static class CountryViewHolder extends RecyclerView.ViewHolder {
        ListItemCountryBinding binding;

        public CountryViewHolder(ListItemCountryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
        if(this.searchQuery != null && !this.searchQuery.isEmpty()) {
            isSearchQueryIsNotEmpty = true;
        }
        else {
            this.searchQuery = null;
            isSearchQueryIsNotEmpty = false;
        }
    }


    private void highlightSearchText(AppCompatTextView textView, String text) {
        if (isSearchQueryIsNotEmpty) {
            textView.setText(text);
            SpannableString spannableString = new SpannableString(text);
            int startIndex = text.toLowerCase().indexOf(searchQuery.toLowerCase());
            while (startIndex >= 0) {
                BackgroundColorSpan highlightSpan = new BackgroundColorSpan(Color.YELLOW);
                spannableString.setSpan(highlightSpan, startIndex, startIndex + searchQuery.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = text.toLowerCase().indexOf(searchQuery.toLowerCase(), startIndex + searchQuery.length());
            }
            textView.setText(spannableString);
        } else {
            textView.setText(text);
        }
    }

}

package com.example.nouno.easydep;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nouno on 20/03/2017.
 */

public class SearchSuggestionAdapter extends ArrayAdapter<SearchSuggestion> {

    public SearchSuggestionAdapter (Context context, ArrayList<SearchSuggestion> searchSuggestions)
    {
        super(context,0,searchSuggestions);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SearchSuggestion searchSuggestion = getItem(position);
        View item = convertView;
        if (item ==null)
        {
            item = LayoutInflater.from(getContext()).inflate(R.layout.search_suggestion_list_item ,parent, false);
        }
        TextView mainText = (TextView)item.findViewById(R.id.main_location_text);
        TextView secondaryText = (TextView)item.findViewById(R.id.secondary_location_text);
        mainText.setText(searchSuggestion.getPrimaryDescription());
        secondaryText.setText(searchSuggestion.getSecondaryDescription());
        //return super.getView(position, convertView, parent);
        return item;
    }

}

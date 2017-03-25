package com.example.nouno.easydep;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nouno on 25/03/2017.
 */

public class OfflineRepairServiceAdapter extends ArrayAdapter<OfflineRepairService> {
    private OnButtonClickListener<String> onButtonClickListener;
    public OfflineRepairServiceAdapter (Context context, ArrayList<OfflineRepairService> repairServices)
    {
        super(context,0,repairServices);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final OfflineRepairService repairService = getItem(position);
        View item = convertView;
        if (item ==null)
        {
            item = LayoutInflater.from(getContext()).inflate(R.layout.offline_repair_service_list_item ,parent, false);
        }
        TextView nameText = (TextView)item.findViewById(R.id.nameText);
        TextView locationText = (TextView)item.findViewById(R.id.locationText);
        TextView priceText = (TextView)item.findViewById(R.id.priceText);
        RatingBar ratingBar = (RatingBar)item.findViewById(R.id.ratingbar);
        View callButton = item.findViewById(R.id.call_button);
        nameText.setText(repairService.getFullName());
        locationText.setText(repairService.getLocation());
        priceText.setText(repairService.getPriceString());
        ratingBar.setRating(repairService.getRating());
        if (onButtonClickListener!=null)
        {
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickListener.onButtonClick(repairService.getPhoneNumber());
            }
        });
        }
        return item;
    }

    public void setOnButtonClickListener(OnButtonClickListener<String> onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }
}

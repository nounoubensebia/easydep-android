package com.example.nouno.easydep;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nouno on 17/03/2017.
 */

public class RepairServiceAdapter extends RecyclerView.Adapter<RepairServiceAdapter.ViewHolder> {
    private OnItemClickListner onItemClickListner;
    private ArrayList<RepairService> repairServices;
    int itemLayout;

    public RepairServiceAdapter(ArrayList<RepairService> repairServices, int itemLayout) {

        this.repairServices = repairServices;
        this.itemLayout = itemLayout;

    }

    public void setOnItemClickListner(OnItemClickListner onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        if (onItemClickListner!=null)
        {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListner.onItemClick(v,viewHolder.getAdapterPosition());
            }
        });
        }
        // return ViewHolder with View
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RepairServiceAdapter.ViewHolder holder, int position) {
        RepairService repairService = repairServices.get(position);
        holder.distanceTextView.setText(repairService.getDistanceString());
        holder.durationTextView.setText(repairService.getDurationString());
        holder.nameTextView.setText(repairService.getFullName());
        if (repairService.isAvailable())
        {
            holder.availableTextView.setText("Disponible");
        }
        else
        {
            holder.availableTextView.setTextColor(Color.parseColor("#F44336"));
            holder.availableTextView.setText("Occupé");
        }

        if (repairService.getPrice()==RepairService.NO_PRICE)
        {
            holder.priceText.setVisibility(View.GONE);

        }
        else
        {
            holder.priceText.setText(repairService.getPriceString());
        }

        holder.locationTextView.setText(repairService.getLocation());
        holder.ratingBar.setRating(repairService.getRating());


    }

    @Override
    public int getItemCount() {
        return repairServices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView distanceTextView;
        private TextView durationTextView;
        private RatingBar ratingBar;
        private TextView availableTextView;
        private TextView locationTextView;
        private TextView nameTextView;
        private TextView priceText;

        public ViewHolder(View itemView) {
            super(itemView);
            distanceTextView = (TextView)itemView.findViewById(R.id.distanceText);
            durationTextView = (TextView)itemView.findViewById(R.id.durationText);
            nameTextView = (TextView)itemView.findViewById(R.id.nameText);
            availableTextView= (TextView)itemView.findViewById(R.id.availableText);
            locationTextView = (TextView)itemView.findViewById(R.id.locationText);
            ratingBar = (RatingBar)itemView.findViewById(R.id.ratingbar);
            priceText = (TextView)itemView.findViewById(R.id.priceText);
        }

    }
}

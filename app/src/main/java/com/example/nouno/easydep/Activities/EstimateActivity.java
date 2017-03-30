package com.example.nouno.easydep.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.nouno.easydep.Data.RepairService;
import com.example.nouno.easydep.Data.RequestEstimate;
import com.example.nouno.easydep.R;
import com.google.gson.Gson;

public class EstimateActivity extends AppCompatActivity {
    private RequestEstimate requestEstimate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retreiveData();
        setContentView(R.layout.activity_estimate);
        //requestEstimate = new RequestEstimate(new RepairService("Bensebia","Noureddine"),5000,2000,null);
        displayData();
    }
    private void retreiveData ()
    {
        Gson gson = new Gson();
        Bundle extras = getIntent().getExtras();
        String json = extras.getString("requestEstimate");
        requestEstimate = gson.fromJson(json,RequestEstimate.class);
    }
    private void displayData ()
    {
        TextView nameText = (TextView)findViewById(R.id.nameText);
        TextView priceText = (TextView)findViewById(R.id.price_text);
        TextView durationText = (TextView)findViewById(R.id.durationText);
        TextView extraInfoText = (TextView)findViewById(R.id.extra_info_text);
        nameText.setText(requestEstimate.getRepairService().getFullName());
        if (requestEstimate.getEstimatedPrice()==RequestEstimate.NO_PRICE)
        {
            priceText.setText("Non spécifié");
        }
        else
        {
            priceText.setText(requestEstimate.getEstimatedPrice()+"");
        }
        if (requestEstimate.getEstimatedTime()==RequestEstimate.NO_TIME)
        {
            durationText.setText("Non spécifié");
        }
        else
        {

            durationText.setText(requestEstimate.getTimeString());
        }
        extraInfoText.setText(requestEstimate.getExtraInfoString());
    }
}

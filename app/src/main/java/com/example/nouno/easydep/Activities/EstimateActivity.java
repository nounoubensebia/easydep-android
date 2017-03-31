package com.example.nouno.easydep.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.nouno.easydep.Data.CarOwner;
import com.example.nouno.easydep.Data.RepairService;
import com.example.nouno.easydep.Data.RequestEstimate;
import com.example.nouno.easydep.DialogUtils;
import com.example.nouno.easydep.GetRepairServiceData;
import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;
import com.example.nouno.easydep.exceptions.ConnectionProblemException;
import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

public class EstimateActivity extends AppCompatActivity {
    private EstimateActivity estimateActivity;
    private RequestEstimate requestEstimate;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        estimateActivity=this;
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
            priceText.setText(requestEstimate.getPriceString());
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
        View repairServiceLayout = findViewById(R.id.repair_service_layout);
        repairServiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRepairServiceData();
            }
        });
    }

    private void getRepairServiceData ()
    {
        long id = requestEstimate.getRepairService().getId();
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("id",id+"");
        GetRepairServiceData getRepairServiceData = new GetRepairServiceData(this);
        getRepairServiceData.getRepairServiceData(map);
    }

}

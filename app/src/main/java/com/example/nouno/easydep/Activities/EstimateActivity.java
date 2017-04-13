package com.example.nouno.easydep.Activities;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nouno.easydep.Data.AssistanceRequestListItem;
import com.example.nouno.easydep.Data.CarOwner;
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
    private long id;
    private boolean acceptedDemande=false;
    private ProgressDialog progressDialog;
    private CarOwner carOwner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        estimateActivity=this;
        super.onCreate(savedInstanceState);
        retreiveData();
        setContentView(R.layout.activity_estimate);
        getEstimate();

    }
    private void getEstimate ()
    {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("estimate_id",id+"");
        GetEstimateTask getEstimateTask = new GetEstimateTask();
        getEstimateTask.execute(map);
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel((int)id);
    }

    private void retreiveData ()
    {
        Bundle extras = getIntent().getExtras();
        id = extras.getLong("estimateId");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String carOwnerJson = sharedPref.getString("carOwner",null);
        carOwner = gson.fromJson(carOwnerJson,CarOwner.class);
    }
    private void displayData ()
    {
        View estimateAcceptedLayout = findViewById(R.id.estimate_accepted);
        View refuseOtherEstimates = findViewById(R.id.refuse_other_estimates);
        View buttons = findViewById(R.id.buttons);
        TextView nameText = (TextView)findViewById(R.id.nameText);
        TextView priceText = (TextView)findViewById(R.id.price_text);

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

        extraInfoText.setText(requestEstimate.getExtraInfoString());
        View repairServiceLayout = findViewById(R.id.repair_service_layout);
        repairServiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRepairServiceData();
            }
        });
        if (acceptedDemande==true)
        {
            buttons.setVisibility(View.GONE);
            if (requestEstimate.getStatus()!=AssistanceRequestListItem.STATUS_IN_QUEUE&&requestEstimate.getStatus()!=AssistanceRequestListItem.STATUS_REPAIR_SERVICE_COMMING)
            refuseOtherEstimates.setVisibility(View.VISIBLE);
            else
                estimateAcceptedLayout.setVisibility(View.VISIBLE);

        }
        else
        {
            buttons.setVisibility(View.VISIBLE);
            Button acceptButton = (Button)findViewById(R.id.accept_button);
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
                    map.put("action",QueryUtils.ACCEPT_ESTIMATE);
                    map.put("assistance_request_id",id+"");
                    AcceptEstimateTask acceptEstimateTask = new AcceptEstimateTask();
                    acceptEstimateTask.execute(map);
                }
            });
            Button refuseButton = (Button)findViewById(R.id.refuse_button);
            refuseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog confirmationDialog = DialogUtils.buildConfirmationDialog("Confirmation",
                            "Voulez vous vraiment refuser ce devis ceci va annuler la demande",
                            estimateActivity, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
                                    map.put("action",QueryUtils.REFUSE_ESTIMATE);
                                    map.put("assistance_request_id",id+"");
                                    RefuseEstimateTask refuseEstimateTask = new RefuseEstimateTask();
                                    refuseEstimateTask.execute(map);
                                }
                            });
                    confirmationDialog.show();


                }
            });
        }

    }



    private void getRepairServiceData ()
    {
        long id = requestEstimate.getRepairService().getId();
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("id",id+"");
        GetRepairServiceData getRepairServiceData = new GetRepairServiceData(this);
        getRepairServiceData.getRepairServiceData(map);
    }

    private class GetEstimateTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
            View root = findViewById(R.id.estimate_layout);
            root.setVisibility(View.GONE);
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            View buttons = findViewById(R.id.buttons);
            buttons.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response=QueryUtils.makeHttpPostRequest(QueryUtils.GET_REQUESTS_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            requestEstimate = new RequestEstimate();
            acceptedDemande =  requestEstimate.parseJson(s);
            View root = findViewById(R.id.estimate_layout);

            root.setVisibility(View.VISIBLE);
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            displayData();
        }
    }

    private class AcceptEstimateTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.SEND_REQUEST_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("success"))
            {
                progressDialog.dismiss();
                Dialog infoDialog = DialogUtils.buildClickableInfoDialog("Opération terminée", "Devis accepté", estimateActivity,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startRequestsListActivity();
                                finish();
                            }
                        });
                infoDialog.show();
            }
        }
    }
    private class RefuseEstimateTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response =QueryUtils.makeHttpPostRequest(QueryUtils.SEND_REQUEST_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("success"))
            {
                progressDialog.dismiss();
                startRequestsListActivity();
                finish();
            }
        }
    }


    private void startRequestsListActivity()
    {

        Intent i = new Intent(this,RequestsListActivity.class);
        startActivity(i);
        finish();
    }
    private void showProgressDialog()
    {
        progressDialog = (ProgressDialog)DialogUtils.buildProgressDialog("Veuillez patienter",estimateActivity);
        progressDialog.show();
    }



}

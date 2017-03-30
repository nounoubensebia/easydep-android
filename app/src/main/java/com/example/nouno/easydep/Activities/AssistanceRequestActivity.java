package com.example.nouno.easydep.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.nouno.easydep.Data.AssistanceRequest;
import com.example.nouno.easydep.DialogUtils;
import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;
import com.example.nouno.easydep.exceptions.ConnectionProblemException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class AssistanceRequestActivity extends AppCompatActivity {
    private AssistanceRequest assistanceRequest;
    private AssistanceRequestActivity assistanceRequestActivity;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        assistanceRequestActivity = this;
        retrieveData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistance_request);
        displayInfo(assistanceRequest);
    }
    private void retrieveData()
    {
        Gson gson = new Gson();
        String json = getIntent().getExtras().getString("assistanceRequest");
        assistanceRequest = gson.fromJson(json,AssistanceRequest.class);
     }

    private void displayInfo (final AssistanceRequest assistanceRequest)
    {
        View upImage = findViewById(R.id.go_button);
        View positionLayout = findViewById(R.id.positionLayout);
        View destinationLayout = findViewById(R.id.destinationLayout);
        final TextView dimensionsTitle = (TextView)findViewById(R.id.dimensionsTitleText);
        final TextView dimensionsText = (TextView)findViewById(R.id.dimensionsText);
        TextView positionText = (TextView)findViewById(R.id.positionText);
        positionText.setText(assistanceRequest.getUserPositon().getLocationName());
        TextView destinationText = (TextView)findViewById(R.id.destinationText);
        if (assistanceRequest.getDestination()!=null)
        {
            Log.i("TAG123",assistanceRequest.getDestination().getLocationName());
            destinationText.setText(assistanceRequest.getDestination().getLocationName());
        }
        Switch heavyWeightSwitch = (Switch)findViewById(R.id.heavy_vehicule_switcher);
        Switch vehiculeCanMove = (Switch)findViewById(R.id.vehicule_can_move_switcher);
        vehiculeCanMove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    assistanceRequest.setVehiculeCanMove(true);
                }
                else
                {
                    assistanceRequest.setVehiculeCanMove(false);
                }
            }
        });
        if (assistanceRequest.isHeavy())
        {
            AssistanceRequest heavyAssistanceRequest =assistanceRequest;
            heavyWeightSwitch.setChecked(true);
            dimensionsTitle.setTextColor(Color.parseColor("#000000"));
            dimensionsText.setText(heavyAssistanceRequest.getDimensionsString());
            dimensionsText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(),RequestDimensionsActivity.class);
                    Gson gson = new Gson();
                    String json = gson.toJson(assistanceRequest);
                    i.putExtra("assistanceRequest",json);
                    startActivity(i);
                }
            });
        }
        else
        {
            dimensionsText.setText("Non spécifié");
        }

         heavyWeightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if (isChecked)
                 {
                     assistanceRequest.setHeavy(true);
                     assistanceRequest.setLength(AssistanceRequest.MIN_LENGTH);
                     assistanceRequest.setWeight(AssistanceRequest.MIN_WEIGHT);
                     Gson gson = new Gson();
                     String json = gson.toJson(assistanceRequest);
                     Intent intent = new Intent(getApplicationContext(),RequestDimensionsActivity.class);
                     intent.putExtra("assistanceRequest",json);
                     startActivity(intent);
                 }
                 else
                 {
                     assistanceRequest.setHeavy(false);
                     dimensionsTitle.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
                     dimensionsText.setText("Non spécifié");
                 }
             }
         });

        positionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchForResults(false);
            }
        });

        destinationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchForResults(true);
            }
        });

        upImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.disableHtmlEscaping();
                Gson gson = gsonBuilder.create();
                String json = gson.toJson(assistanceRequest);
                SendRequestTask sendRequestTask = new SendRequestTask();
                sendRequestTask.execute(json);
            }
        });

    }

    private void searchForResults (boolean destination)
    {
        Intent i = new Intent(getApplicationContext(),ManualSearchActivity.class);
        Gson gson = new Gson();
        String json = gson.toJson(assistanceRequest);
        i.putExtra("assistanceRequest",json);
        i.putExtra("destination",destination);
        startActivity(i);
    }

    private class SendRequestTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog)DialogUtils.buildProgressDialog("Envoi de la demande",assistanceRequestActivity);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String s = null;
            try {
                s = QueryUtils.makeHttpPostJsonRequest(QueryUtils.SEND_REQUEST_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return s;
        }

        @Override
        protected void onPostExecute(String s) {

            progressDialog.dismiss();
            Dialog infoDialog = DialogUtils.buildClockableInfoDialog("", "Votre demande a été envoyée", assistanceRequestActivity, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(getApplicationContext(),RequestsListActivity.class);
                    startActivity(i);
                }
            });
            infoDialog.show();
        }
    }

}

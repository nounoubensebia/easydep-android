package com.example.nouno.easydep;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.nouno.easydep.Activities.RepairServiceInfoActivity;
import com.example.nouno.easydep.Data.RepairService;
import com.example.nouno.easydep.exceptions.ConnectionProblemException;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by nouno on 31/03/2017.
 */

public class GetRepairServiceData {
    ProgressDialog progressDialog;
    Context context;

    public GetRepairServiceData(Context context) {
        this.context = context;
    }

    public void getRepairServiceData (Map<String,String> map) {
        GetRepairServiceDataTask getRepairServiceDataTask = new GetRepairServiceDataTask();
        getRepairServiceDataTask.execute(map);
    }

    private class GetRepairServiceDataTask extends AsyncTask<Map<String,String>,Void,String>
    {
        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog)DialogUtils.buildProgressDialog("Chargement des données du dépanneur",context);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.LOCAL_GET_REPAIR_SERVICES_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            RepairService repairService = RepairService.parseJson(s);
            startInfoActivity(repairService);
        }

    }
    private void startInfoActivity (RepairService repairService)
    {
        Gson gson = new Gson();
        String json = gson.toJson(repairService);
        Intent i = new Intent(context,RepairServiceInfoActivity.class);
        i.putExtra("repairService",json);
        context.startActivity(i);
    }
}

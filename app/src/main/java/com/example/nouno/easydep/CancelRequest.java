package com.example.nouno.easydep;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.nouno.easydep.Activities.RequestsListActivity;
import com.example.nouno.easydep.exceptions.ConnectionProblemException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nouno on 07/04/2017.
 */

public class CancelRequest {
    public ProgressDialog progressDialog;
    public Context context;
    public RequestsListActivity requestsListActivity;

    public CancelRequest(Context context) {
        this.context = context;
    }

    public CancelRequest(Context context, RequestsListActivity requestsListActivity) {
        this.context = context;
        this.requestsListActivity = requestsListActivity;
    }

    public void cancelRequest (final long assistanceRequestId)
    {
        Dialog dialog = DialogUtils.buildConfirmationDialog("Confirmation", "Voulez vous vraiment annuler cette demande ceci" +
                "va entrainer la suppression de la demande", context, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               startCancelRequestTask(assistanceRequestId);

            }
        });
        dialog.show();
    }

    public void startCancelRequestTask(long assistanceRequestId)
    {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("assistance_request_id",assistanceRequestId+"");
        map.put("action",QueryUtils.CANCEL_REQUEST);
        CancelRequestTask cancelRequestTask = new CancelRequestTask();
        cancelRequestTask.execute(map);
    }

    private class CancelRequestTask extends AsyncTask<Map<String,String>,Void,String>
    {
        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog) DialogUtils.buildProgressDialog("Veuillez patienter",context);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response =QueryUtils.makeHttpPostRequest(QueryUtils.REQUESTS_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            Dialog infoDialog = DialogUtils.buildInfoDialog("Opération terminée","Demande annulée",context);
            infoDialog.show();
            if (requestsListActivity!=null)
            requestsListActivity.loadRequestsList();
            else
            {
                Intent i = new Intent(context.getApplicationContext(),RequestsListActivity.class);
                context.startActivity(i);
            }
        }
    }
}

package com.example.nouno.easydep.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.nouno.easydep.CancelRequest;
import com.example.nouno.easydep.Data.AssistanceRequest;
import com.example.nouno.easydep.Data.AssistanceRequestListItem;
import com.example.nouno.easydep.Data.CarOwner;
import com.example.nouno.easydep.Data.RepairService;
import com.example.nouno.easydep.DialogUtils;
import com.example.nouno.easydep.GetRepairServiceData;
import com.example.nouno.easydep.ListAdapters.AssistanceRequestAdapter;
import com.example.nouno.easydep.OnButtonClickListener;
import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;
import com.example.nouno.easydep.exceptions.ConnectionProblemException;
import com.google.gson.Gson;
import com.yayandroid.theactivitymanager.TAMBaseActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestsListActivity extends TAMBaseActivity {
    private ListView requestsList;
    private ArrayList<AssistanceRequestListItem> assistanceRequestListItems;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private RequestsListActivity requestsListActivity;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadRequestsList();
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,new IntentFilter("update_requests_list"));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_list);
        requestsList = (ListView)findViewById(R.id.list);
        requestsListActivity = this;
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refrech_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1,R.color.refresh_progress_2,R.color.refresh_progress_3);
        loadRequestsList();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRequestsList();
            }
        });

    }

    public void loadRequestsList ()
    {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("carOwnerId",loadUserData().getId()+"");
        GetRequestsTask getRequestsTask = new GetRequestsTask();
        getRequestsTask.execute(map);
    }

    private CarOwner loadUserData ()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPref.getString("carOwner",null);
        CarOwner carOwner = gson.fromJson(json,CarOwner.class);
        return carOwner;
    }

    private void populateRequestsList (final ArrayList<AssistanceRequestListItem> assistanceRequestListItems)
    {

        AssistanceRequestAdapter assistanceRequestAdapter = new AssistanceRequestAdapter(this,assistanceRequestListItems);
        assistanceRequestAdapter.setOnEstimateClickListner(new OnButtonClickListener<AssistanceRequestListItem>() {
            @Override
            public void onButtonClick(AssistanceRequestListItem assistanceRequest) {
                //Gson gson = new Gson();
                //String json = gson.toJson(assistanceRequest);
                Intent i = new Intent(getApplicationContext(),EstimateActivity.class);
                i.putExtra("estimateId",assistanceRequest.getId());
                startActivity(i);
            }
        });
        assistanceRequestAdapter.setOnRepairServiceClickListener(new OnButtonClickListener<RepairService>() {
            @Override
            public void onButtonClick(RepairService repairService) {
                getRepairServiceData(repairService);
            }
        });
        assistanceRequestAdapter.setOnRequestClickListner(new OnButtonClickListener<AssistanceRequest>() {
            @Override
            public void onButtonClick(AssistanceRequest assistanceRequest) {
                showRequest(assistanceRequest);
            }
        });
        assistanceRequestAdapter.setOnCancelClickListner(new OnButtonClickListener<AssistanceRequest>() {
            @Override
            public void onButtonClick(final AssistanceRequest assistanceRequest) {

                CancelRequest cancelRequest = new CancelRequest(requestsListActivity,requestsListActivity);
                cancelRequest.cancelRequest(assistanceRequest.getId());

            }
        });
        assistanceRequestAdapter.setOnDontShowClickListner(new OnButtonClickListener<AssistanceRequest>() {
            @Override
            public void onButtonClick(AssistanceRequest assistanceRequest) {
                DontShowRequestTask dontShowRequestTask = new DontShowRequestTask();
                LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
                map.put("action","change_status");
                map.put("assistance_request_id",assistanceRequest.getId()+"");
                map.put("status",5+"");
                dontShowRequestTask.execute(map);
            }
        });
        assistanceRequestAdapter.setOnDeleteClickListner(new OnButtonClickListener<AssistanceRequest>() {
            @Override
            public void onButtonClick(AssistanceRequest assistanceRequest) {

                deleteRequest(assistanceRequest);
            }
        });

        assistanceRequestAdapter.setOnQueueClickListner(new OnButtonClickListener<AssistanceRequestListItem>() {
            @Override
            public void onButtonClick(AssistanceRequestListItem assistanceRequestListItem) {
                Intent i = new Intent(getApplicationContext(),QueueActivity.class);
                if (assistanceRequestListItem.getStatus() == assistanceRequestListItem.STATUS_REPAIR_SERVICE_COMMING)
                {
                    i.putExtra("position",0);
                    i.putExtra("assistanceRequest",assistanceRequestListItem.toJson());
                }
                if (assistanceRequestListItem.getStatus() == assistanceRequestListItem.STATUS_IN_QUEUE)
                {
                    i.putExtra("position",assistanceRequestListItem.getNumberOfPeopleBefore());
                    i.putExtra("assistanceRequest",assistanceRequestListItem.toJson());
                }
                if (assistanceRequestListItem.getStatus() == assistanceRequestListItem.STATUS_COMPLETED)
                {
                    i.putExtra("position",-2);
                    i.putExtra("assistanceRequest",assistanceRequestListItem.toJson());
                }
                startActivity(i);
            }
        });
        requestsList.setAdapter(assistanceRequestAdapter);
        requestsList.setDividerHeight(0);
    }

    private void deleteRequest (AssistanceRequest assistanceRequest)
    {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("assistance_request_id",assistanceRequest.getId()+"");
        map.put("action",QueryUtils.DELETE_REQUEST);
        DeleteRequestTask cancelRequestTask = new DeleteRequestTask();
        cancelRequestTask.execute(map);
    }





    private void showRequest(AssistanceRequest assistanceRequest)
    {
        Gson gson = new Gson();
        String json = gson.toJson(assistanceRequest);
        Intent i = new Intent(this,AssistanceRequestActivity.class);
        i.putExtra("assistanceRequest",json);
        i.putExtra("requestSent",true);
        startActivity(i);
    }

    private void getRepairServiceData (RepairService repairService)
    {
        long id = repairService.getId();
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("id",id+"");
        GetRepairServiceData getRepairServiceData = new GetRepairServiceData(this);
        getRepairServiceData.getRepairServiceData(map);
    }
    private class GetRequestsTask extends AsyncTask<Map<String,String>,Void,String>
    {
        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
            requestsList.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.GET_REQUESTS_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            swipeRefreshLayout.setRefreshing(false);
            assistanceRequestListItems = AssistanceRequestListItem.parseJson(s);

            populateRequestsList(assistanceRequestListItems);
            requestsList.setVisibility(View.VISIBLE);
        }
    }



    private class DeleteRequestTask extends AsyncTask<Map<String,String>,Void,String>
    {
        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog) DialogUtils.buildProgressDialog("Veuillez patienter",requestsListActivity);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String s = null;
            try {
                s = QueryUtils.makeHttpPostRequest(QueryUtils.REQUESTS_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            loadRequestsList();
        }
    }

    private class DontShowRequestTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog) DialogUtils.buildProgressDialog("Veuillez patienter...",requestsListActivity);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.REQUESTS_URL,params[0]);
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
                Dialog dialog = DialogUtils.buildClickableInfoDialog("Opération terminée", "Cette demande ne sera plus affichée", requestsListActivity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(),RequestsListActivity.class);
                        startActivity(i);
                    }
                });
                dialog.show();
                progressDialog.dismiss();
            }
            else
            {

            }
        }
    }




}

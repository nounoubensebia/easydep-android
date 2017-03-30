package com.example.nouno.easydep.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.nouno.easydep.Data.AssistanceRequestListItem;
import com.example.nouno.easydep.Data.CarOwner;
import com.example.nouno.easydep.Data.RepairService;
import com.example.nouno.easydep.Data.RequestEstimate;
import com.example.nouno.easydep.ListAdapters.AssistanceRequestAdapter;
import com.example.nouno.easydep.OnButtonClickListener;
import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;
import com.example.nouno.easydep.exceptions.ConnectionProblemException;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestsListActivity extends AppCompatActivity {
    private ListView requestsList;
    private ArrayList<AssistanceRequestListItem> assistanceRequestListItems;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_list);
        requestsList = (ListView)findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refrech_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1,R.color.refresh_progress_2,R.color.refresh_progress_3);
        RepairService repairService = new RepairService(2,"Bensebia","Noureddine","El mohamadia","05987564",2,100);
        RepairService repairService1 = new RepairService(3,"haha","goku","qsd","465564",3,1000);
        //assistanceRequestListItems = new ArrayList<>();
        //assistanceRequestListItems.add(new AssistanceRequestListItem(repairService,AssistanceRequestListItem.STATUS_QUOTATION_RECEIVED,1000));
        //assistanceRequestListItems.add(new AssistanceRequestListItem(repairService1,AssistanceRequestListItem.STATUS_WAITING_CONFIRMATION,1000));
        //assistanceRequestListItems.add(new AssistanceRequestListItem(repairService1,AssistanceRequestListItem.STATUS_WAITING_QUOTATION,1000));
        //populateRequestsList(assistanceRequestListItems);
        loadRequestsList();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRequestsList();
            }
        });

    }

    private void loadRequestsList ()
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

    private void populateRequestsList (ArrayList<AssistanceRequestListItem> assistanceRequestListItems)
    {
        //test
        RepairService repairService = new RepairService("Bensbeia","Noureddine");
        assistanceRequestListItems.add(new AssistanceRequestListItem(repairService,AssistanceRequestListItem.STATUS_QUOTATION_RECEIVED,150000,
                new RequestEstimate(repairService,1000,2000,"")));

        //test
        AssistanceRequestAdapter assistanceRequestAdapter = new AssistanceRequestAdapter(this,assistanceRequestListItems);
        assistanceRequestAdapter.setOnEstimateClickListner(new OnButtonClickListener<RequestEstimate>() {
            @Override
            public void onButtonClick(RequestEstimate requestEstimate) {
                Gson gson = new Gson();
                String json = gson.toJson(requestEstimate);
                Intent i = new Intent(getApplicationContext(),EstimateActivity.class);
                i.putExtra("requestEstimate",json);
                startActivity(i);
            }
        });
        requestsList.setAdapter(assistanceRequestAdapter);
        requestsList.setDividerHeight(0);
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


}
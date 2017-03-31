package com.example.nouno.easydep.Data;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nouno on 30/03/2017.
 */

public class AssistanceRequestListItem extends AssistanceRequest {
    private int status;
    private long time;
    private RequestEstimate requestEstimate;

    public static final int STATUS_WAITING_QUOTATION = 0;
    public static final int STATUS_QUOTATION_RECEIVED = 1;
    public static final int STATUS_WAITING_CONFIRMATION = 2;
    public static final int STATUS_REQUEST_CONFIRMED = 3;

    public AssistanceRequestListItem(RepairService repairService, int status,long time) {
        super(repairService);
        this.status = status;
        this.time = time;
    }

    public AssistanceRequestListItem(RepairService repairService, int status, long time, RequestEstimate requestEstimate) {
        super(repairService);
        this.status = status;
        this.time = time;
        this.requestEstimate = requestEstimate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTimeString ()
    {
        DateFormat dateFormat = new SimpleDateFormat();
        Date date = new Date(time*1000);
        return dateFormat.format(date);

    }

    public RequestEstimate getRequestEstimate() {
        return requestEstimate;
    }

    public void setRequestEstimate(RequestEstimate requestEstimate) {
        this.requestEstimate = requestEstimate;
    }

    public static ArrayList<AssistanceRequestListItem> parseJson (String json)
    {
        ArrayList<AssistanceRequestListItem> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject repairJson = jsonObject.getJSONObject("repair_service");
                String firstname = repairJson.getString("firstname");
                String lastname = repairJson.getString("lastname");
                long id =repairJson.getLong("id");

                RepairService repairService =new RepairService(id,firstname,lastname);
                long time = jsonObject.getLong("time");
                int status = jsonObject.getInt("status");
                AssistanceRequestListItem assistanceRequestListItem = new AssistanceRequestListItem(repairService,status,time);

                if (jsonObject.has("estimate"))
                {
                    JSONObject estimate = jsonObject.getJSONObject("estimate");
                    String extraInfo = estimate.getString("comment");
                    long price = estimate.getLong("price");
                    long duration = estimate.getLong("duration");
                    RequestEstimate requestEstimate = new RequestEstimate(repairService,price,duration,extraInfo);
                    assistanceRequestListItem.setRequestEstimate(requestEstimate);
                }
                list.add(assistanceRequestListItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sortByTime(list);
        return list;

    }
    public static void sortByTime (ArrayList<AssistanceRequestListItem> list)
    {
        for (int i=0;i<list.size();i++)
        {

            for (int j=i+1;j<list.size();j++)
            {

                if (list.get(i).time<list.get(j).time)
                {
                    AssistanceRequestListItem comment1 = list.get(i);
                    AssistanceRequestListItem comment2 = list.get(j);
                    list.set(i,comment2);
                    list.set(j,comment1);
                }
            }
        }
    }
}

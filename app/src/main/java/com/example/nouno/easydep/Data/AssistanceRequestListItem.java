package com.example.nouno.easydep.Data;

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





    public AssistanceRequestListItem(boolean vehiculeCanMove, Position userPositon, Position destination, CarOwner carOwner, RepairService repairService, float length, float weight, int status, long time, RequestEstimate requestEstimate) {
        super(vehiculeCanMove, userPositon, destination, carOwner, repairService, length, weight);
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
                String departure = jsonObject.getString("departure");
                String destination = jsonObject.getString("destination");

                boolean vehiculeCanMove = false;
                float length = AssistanceRequest.NOT_HEAVY;
                float weight = AssistanceRequest.NOT_HEAVY;
                if (jsonObject.getInt("vehicule_can_move")==1)
                    vehiculeCanMove=true;
                if (jsonObject.has("length"))
                length=(float)jsonObject.getDouble("length");
                if (jsonObject.has("weight"))
                    weight=(float)jsonObject.getDouble("weight");
                AssistanceRequestListItem assistanceRequestListItem = new AssistanceRequestListItem(vehiculeCanMove,new Position(departure,-1,-1),new Position(destination,-1,-1),
                        null,repairService,length,weight,status,time,null);
                if (destination.equals("null"))
                    assistanceRequestListItem.setDestination(null);

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
        sort(list);
        return list;

    }
    public static void sort(ArrayList<AssistanceRequestListItem> list)
    {
        for (int i=0;i<list.size();i++)
        {

            for (int j=i+1;j<list.size();j++)
            {

                if ((list.get(i).time<list.get(j).time&&list.get(i).getStatus()==list.get(j).getStatus())||(list.get(i).getStatus()<list.get(j).getStatus()))
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

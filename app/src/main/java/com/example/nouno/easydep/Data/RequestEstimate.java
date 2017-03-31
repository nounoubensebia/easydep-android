package com.example.nouno.easydep.Data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nouno on 30/03/2017.
 */

public class RequestEstimate {
    private RepairService repairService;
    private long estimatedPrice;
    private long estimatedTime;
    private String extraInfoString;
    private int status;
    public static final int NO_PRICE = -1;
    public static final int NO_TIME = -1;

    public RequestEstimate(RepairService repairService, long estimatedPrice, long estimatedTime, String extraInfoString,int status) {
        this.repairService = repairService;
        this.estimatedPrice = estimatedPrice;
        this.estimatedTime = estimatedTime;
        this.extraInfoString = extraInfoString;
        this.status = status;
    }
    public RequestEstimate()
    {

    }

    public RepairService getRepairService() {
        return repairService;
    }

    public long getEstimatedPrice() {
        return estimatedPrice;
    }

    public long getEstimatedTime() {
        return estimatedTime;
    }

    public String getExtraInfoString() {
        return extraInfoString;
    }

    public String getPriceString ()
    {
        return estimatedPrice+" Da";
    }

    public String getTimeString ()
    {
        return (estimatedTime/60)+" Minutes";
    }

    public boolean parseJson (String json)
    {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String extra = null;
            if (!jsonObject.isNull("comment"))
            {
                extra = jsonObject.getString("comment");
            }
            long price = jsonObject.getLong("price");
            long duration = jsonObject.getLong("duration");
            int status = jsonObject.getInt("status");
            JSONObject repairServiceJson = jsonObject.getJSONObject("repair_service");
            long id = repairServiceJson.getLong("id");
            String firstname = repairServiceJson.getString("first_name");
            String lastname = repairServiceJson.getString("last_name");
            RepairService repairService = new RepairService(id,firstname,lastname);
            //this = new RequestEstimate(repairService,price,duration,extra,status);
            this.repairService=repairService;
            this.estimatedPrice=price;
            this.estimatedTime=duration;
            this.extraInfoString=extra;
            this.status=status;
            return jsonObject.getBoolean("accepted_demande");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

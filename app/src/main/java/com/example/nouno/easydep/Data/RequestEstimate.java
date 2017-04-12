package com.example.nouno.easydep.Data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nouno on 30/03/2017.
 */

public class RequestEstimate {
    private RepairService repairService;
    private long estimatedPrice;

    private String extraInfoString;
    private int status;
    public static final int NO_PRICE = -1;
    public static final int NO_TIME = -1;

    public RequestEstimate(RepairService repairService, long estimatedPrice, String extraInfoString,int status) {
        this.repairService = repairService;
        this.estimatedPrice = estimatedPrice;

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



    public String getExtraInfoString() {
        return extraInfoString;
    }

    public String getPriceString ()
    {
        return estimatedPrice+" Da";
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

            int status = jsonObject.getInt("status");
            JSONObject repairServiceJson = jsonObject.getJSONObject("repair_service");
            long id = repairServiceJson.getLong("id");
            String firstname = repairServiceJson.getString("first_name");
            String lastname = repairServiceJson.getString("last_name");
            RepairService repairService = new RepairService(id,firstname,lastname);
            //this = new RequestEstimate(repairService,price,duration,extra,status);
            this.repairService=repairService;
            this.estimatedPrice=price;

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

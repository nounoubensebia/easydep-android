package com.example.nouno.easydep.Data;

/**
 * Created by nouno on 30/03/2017.
 */

public class RequestEstimate {
    private RepairService repairService;
    private long estimatedPrice;
    private long estimatedTime;
    private String extraInfoString;

    public static final int NO_PRICE = -1;
    public static final int NO_TIME = -1;

    public RequestEstimate(RepairService repairService, long estimatedPrice, long estimatedTime, String extraInfoString) {
        this.repairService = repairService;
        this.estimatedPrice = estimatedPrice;
        this.estimatedTime = estimatedTime;
        this.extraInfoString = extraInfoString;
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

    public String getTimeString ()
    {
        return (estimatedTime/60)+" Minutes";
    }
}

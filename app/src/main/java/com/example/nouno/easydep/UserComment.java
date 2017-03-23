package com.example.nouno.easydep;

import java.util.Date;

/**
 * Created by nouno on 23/03/2017.
 */

public class UserComment {
    private CarOwner carOwner;
    private RepairService repairService;
    private int rating;
    private String comment;
    private Date date;
    private boolean fromConnectedUser;

    public UserComment(CarOwner carOwner,int rating, String comment, Date date, boolean fromConnectedUser) {
        this.carOwner = carOwner;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.fromConnectedUser = fromConnectedUser;
    }

    public CarOwner getCarOwner() {
        return carOwner;
    }

    public RepairService getRepairService() {
        return repairService;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Date getDate() {
        return date;
    }

    public boolean isFromConnectedUser() {
        return fromConnectedUser;
    }
}

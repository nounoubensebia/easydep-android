package com.example.nouno.easydep.Data;

/**
 * Created by nouno on 28/03/2017.
 */

public class AssistanceRequest {
    private boolean heavy;
    private boolean vehiculeCanMove;
    private Position userPositon;
    private Position destination;
    private CarOwner carOwner;
    private RepairService repairService;
    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 15;
    public static final int MIN_WEIGHT = 6;
    public static final int MAX_WEIGHT = 12;
    public static final int DONT_KNOW = -1;
    public static final int NOT_HEAVY = -2;
    private float length;
    private float weight;

    public AssistanceRequest(boolean vehiculeCanMove, Position userPositon, Position destination, CarOwner carOwner, RepairService repairService) {
        this.heavy = false;
        this.vehiculeCanMove = vehiculeCanMove;
        this.userPositon = userPositon;
        this.destination = destination;
        this.carOwner = carOwner;
        this.repairService = repairService;
    }

    public AssistanceRequest(boolean vehiculeCanMove, Position userPositon, Position destination, CarOwner carOwner, RepairService repairService, float length, float weight) {
        this.heavy = true;
        this.vehiculeCanMove = vehiculeCanMove;
        this.userPositon = userPositon;
        this.destination = destination;
        this.carOwner = carOwner;
        this.repairService = repairService;
        this.length = length;
        this.weight = weight;
    }

    public CarOwner getCarOwner() {
        return carOwner;
    }

    public void setCarOwner(CarOwner carOwner) {
        this.carOwner = carOwner;
    }

    public RepairService getRepairService() {
        return repairService;
    }

    public void setRepairService(RepairService repairService) {
        this.repairService = repairService;
    }

    public boolean isVehiculeCanMove() {
        return vehiculeCanMove;
    }

    public void setVehiculeCanMove(boolean vehiculeCanMove) {
        this.vehiculeCanMove = vehiculeCanMove;
    }

    public Position getUserPositon() {
        return userPositon;
    }

    public void setUserPositon(Position userPositon) {
        this.userPositon = userPositon;
    }

    public Position getDestination() {
        return destination;
    }

    public void setDestination(Position destination) {
        this.destination = destination;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean isHeavy() {
        return heavy;
    }

    public void setHeavy(boolean heavy) {
        this.heavy = heavy;
    }

    public String getLengthString ()
    {
        if (length==DONT_KNOW)
        {
            return "Non spécifiée";
        }
        else
        {
            return length+" Metres";
        }
    }

    public String getWeightString ()
    {
        if (weight==DONT_KNOW)
        {
            return "Non spécifié";
        }
        else
        {
            return weight+" Tonnes";
        }
    }

    public String getDimensionsString () {
        if (weight==DONT_KNOW&&length==DONT_KNOW)
        {
            return "Non spécifiés";
        }

        return "Longeur : "+getLengthString()+", "+"Poids : "+getWeightString();
    }
}

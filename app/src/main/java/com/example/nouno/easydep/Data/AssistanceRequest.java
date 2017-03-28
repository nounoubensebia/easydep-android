package com.example.nouno.easydep.Data;

/**
 * Created by nouno on 28/03/2017.
 */

public class AssistanceRequest {
    private boolean vehiculeCanMove;
    private Position userPositon;
    private Position destination;

    public AssistanceRequest(boolean vehiculeCanMove, Position userPositon, Position destination) {
        this.vehiculeCanMove = vehiculeCanMove;
        this.userPositon = userPositon;
        this.destination = destination;
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
}

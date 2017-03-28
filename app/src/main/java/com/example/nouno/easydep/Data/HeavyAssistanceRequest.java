package com.example.nouno.easydep.Data;

/**
 * Created by nouno on 28/03/2017.
 */

public class HeavyAssistanceRequest extends AssistanceRequest {
    private float lenght;
    private float weight;

    public HeavyAssistanceRequest(boolean vehiculeCanMove, Position userPositon, Position destination, float lenght, float weight) {
        super(vehiculeCanMove, userPositon, destination);
        this.lenght = lenght;
        this.weight = weight;
    }

    public float getLenght() {
        return lenght;
    }

    public void setLenght(float lenght) {
        this.lenght = lenght;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}

package com.example.nouno.easydep;

/**
 * Created by nouno on 02/03/2017.
 */

public class CarOwner extends Person {

    private String email;

    public CarOwner(long id,String firstName, String lastName,String email) {
        super(id,firstName,lastName);
        this.email = email;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

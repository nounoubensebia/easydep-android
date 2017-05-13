package com.example.nouno.easydep.Data;

import com.example.nouno.easydep.Data.Person;

/**
 * Created by nouno on 02/03/2017.
 */

public class CarOwner extends Person {

    private String email;
    private Tokens tokens;
    public CarOwner(long id,String firstName, String lastName,String email,Tokens tokens) {
        super(id,firstName,lastName);
        this.email = email;
        this.tokens = tokens;
    }



    public Tokens getTokens() {
        return tokens;
    }

    public void setTokens(Tokens tokens) {
        this.tokens = tokens;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

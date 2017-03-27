package com.example.nouno.easydep;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nouno on 02/03/2017.
 */

public class Person {
    private String firstName,lastName;
    private String email;
    private Position position;
    private long id;

    public Person(long id,String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id=id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {return firstName+" "+lastName;}

    public Position getPosition() {
        return position;
    }

    public long getId() {
        return id;
    }

    public static Person fromJson(String json)
    {
        Person person = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            long id = jsonObject.getLong("id");
            String firstname = jsonObject.getString("first_name");
            String lastname = jsonObject.getString("last_name");
            person = new CarOwner(id,firstname,lastname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return person;
    }
}

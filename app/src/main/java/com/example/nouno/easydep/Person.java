package com.example.nouno.easydep;

/**
 * Created by nouno on 02/03/2017.
 */

public class Person {
    private String firstName,lastName;
    private String email;
    private Position position;

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
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

    public Position getPosition() {
        return position;
    }
}

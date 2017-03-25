package com.example.nouno.easydep;

/**
 * Created by nouno on 25/03/2017.
 */

public class OfflineRepairService extends Person {
    private String location;
    private String phoneNumber;
    private float rating;
    private int price;
    public static final int NO_PRICE = 99999;
    public static final String NO_PRICE_STRING = "Tarifs non communiqués";


    public OfflineRepairService(long id, String firstName, String lastName, String location, String phoneNumber, float rating, int price) {
        super(id, firstName, lastName);
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public float getRating() {
        return rating;
    }

    public int getPrice() {
        return price;
    }

    public String getPriceString () {
        if (price!=NO_PRICE)
        {
            return price+"Da/KM";
        }
        else
        {
            return NO_PRICE_STRING;
        }
    }
}

package com.example.nouno.easydep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by nouno on 02/03/2017.
 */

public class RepairService {
    private String firstName,lastName,location;
    private boolean available;
    private int duration;
    private double distance;
    private  double latitude;
    private  double longitude;
    private float rating;

    public RepairService(String firstName, String lastName, String location, boolean available, int duration, int distance,double latitude,double longitude,float rating) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.available = available;
        this.duration = duration;
        this.distance = distance;
        this.latitude=latitude;
        this.longitude=longitude;
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLocation() {
        return location;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    public String getDistanceString ()
    {
        double distanceInKm = distance/1000;
        NumberFormat nf;
        String s;
        if (distanceInKm <10)
        {nf = new DecimalFormat("0.#");
            s = nf.format(distanceInKm);}
        else
        {
            int dist = (int)distanceInKm;
            s=dist+"";
        }




        return (s+"KM");
    }
    public String getDurationString ()
    {
        double durationInMinutes = duration/60;
        NumberFormat nf = new DecimalFormat("0.#");
        String s = nf.format(durationInMinutes);
        return ("a "+s+" Minutes de route");

    }
    public static ArrayList<RepairService> parseJson  (String jsonString)
    {   ArrayList<RepairService> repairServices = new ArrayList<>();
        try {
            JSONArray jsonArray= new JSONArray(jsonString);
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String firstname = jsonObject.getString("first_name");
                String lastname=jsonObject.getString("last_name");
                Double latitude = jsonObject.getDouble("latitude");
                Double longitude = jsonObject.getDouble("longitude");
                String location = jsonObject.getString("location");
                int duration = jsonObject.getInt("duration");
                int distance = jsonObject.getInt("distance");
                float rating = (float)jsonObject.getDouble("rating");
                boolean available = jsonObject.getBoolean("available");
                RepairService repairService = new RepairService(firstname,lastname,location,available,duration,distance,latitude,longitude,rating);
                repairServices.add(repairService);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return repairServices;

        }
        return repairServices;


    }
}

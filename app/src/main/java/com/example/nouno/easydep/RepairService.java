package com.example.nouno.easydep;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by nouno on 02/03/2017.
 */

public class RepairService extends Person {
    private String location;
    private String phoneNumber;

    private boolean available;
    private int duration;
    private double distance;
    private  double latitude;
    private  double longitude;
    private float rating;
    private int price;
    public static final int NO_PRICE = 99999;
    public static final String NO_PRICE_STRING = "Tarifs non communiqués";

    public RepairService(long id,String firstName, String lastName, String location, boolean available, int duration, double distance,double latitude,double longitude,float rating,int price,String phoneNumber) {
        super(id,firstName,lastName);
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.available = available;
        this.duration = duration;
        this.distance = distance;
        this.latitude=latitude;
        this.longitude=longitude;
        this.rating = rating;
        this.price = price;
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



    public int getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
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

    public boolean isAvailable() {
        return available;
    }

    public int getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setRating(float rating) {
        this.rating = rating;
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
                long id = jsonObject.getLong("id");
                String phoneNumber = jsonObject.getString("phone_number");
                int price = NO_PRICE;
                if (!jsonObject.isNull("price"))
                {
                    price = jsonObject.getInt("price");
                }

                RepairService repairService = new RepairService(id,firstname,lastname,location,available,duration,distance,latitude,longitude,rating,price,phoneNumber);
                repairServices.add(repairService);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return repairServices;

        }
        return repairServices;


    }

    public static void sortByDistance (ArrayList<RepairService> repairServices)
    {
        for (int i=0;i<repairServices.size();i++)
        {

            for (int j=i+1;j<repairServices.size();j++)
            {

                if (repairServices.get(i).distance>repairServices.get(j).distance)
                {
                    RepairService repairService1 = repairServices.get(i);
                    RepairService repairService2 = repairServices.get(j);
                    repairServices.set(i,repairService2);
                    repairServices.set(j,repairService1);
                }
            }
        }
    }
    public static void sortByRating (ArrayList<RepairService> repairServices)
    {
        for (int i=0;i<repairServices.size();i++)
        {

            for (int j=i+1;j<repairServices.size();j++)
            {

                if (repairServices.get(i).rating<repairServices.get(j).rating)
                {
                    RepairService repairService1 = repairServices.get(i);
                    RepairService repairService2 = repairServices.get(j);
                    repairServices.set(i,repairService2);
                    repairServices.set(j,repairService1);

                }
            }
        }
    }

    public static void sortByPrice (ArrayList<RepairService> repairServices)
    {
        for (int i=0;i<repairServices.size();i++)
        {

            for (int j=i+1;j<repairServices.size();j++)
            {

                if (repairServices.get(i).price>repairServices.get(j).price)
                {
                    RepairService repairService1 = repairServices.get(i);
                    RepairService repairService2 = repairServices.get(j);
                    repairServices.set(i,repairService2);
                    repairServices.set(j,repairService1);

                }
            }
        }

    }

    public static void deleteNotAvailable (ArrayList<RepairService> repairServices)
    {
        for (int i=0;i<repairServices.size();i++)
        {
            RepairService repairService = repairServices.get(i);

            if (!repairService.isAvailable())
            {
                repairServices.remove(repairService);
                i--;
            }
        }
    }

    public static void filtrePrice (int price,ArrayList<RepairService> repairServices)
    {
        for (int i=0;i<repairServices.size();i++)
        {
            RepairService repairService = repairServices.get(i);

            if (price < repairService.price)
            {
                repairServices.remove(repairService);
                i--;
            }
        }

    }

    public static void filtreRating (float rating,ArrayList<RepairService> repairServices)
    {
        //ArrayList<RepairService> availableRepairServices = new ArrayList<>();
        for (int i=0;i<repairServices.size();i++)
        {
            RepairService repairService = repairServices.get(i);
            if (rating > repairService.rating)
            {
                repairServices.remove(repairService);
                i--;
            }
        }
        //repairServices = new ArrayList<>(availableRepairServices);

    }




    public static void applyFiltre (ArrayList<RepairService> repairServices,Filtre filtre)
    {
        switch (filtre.getSortingMethod())
        {
            case Filtre.SORT_BY_DISTANCE : sortByDistance(repairServices);
                break;
            case Filtre.SORT_BY_PRICE : sortByPrice(repairServices);
                break;
            case Filtre.SORT_BY_RATING : sortByRating(repairServices);
        }
        filtrePrice(filtre.getMaxPrice(),repairServices);
        filtreRating(filtre.getMinRating(),repairServices);
        if (!filtre.isShowNotAvailable())
        {
            deleteNotAvailable(repairServices);
        }
    }
}

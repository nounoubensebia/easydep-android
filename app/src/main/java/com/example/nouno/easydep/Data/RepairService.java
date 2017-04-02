package com.example.nouno.easydep.Data;

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
    private int numberOfPeopleInqueue;
    private boolean available;
    private int duration;
    private double distance;
    private  double latitude;
    private  double longitude;
    private float rating;
    private int price;
    public static final int NO_PRICE = 99999;
    public static final String NO_PRICE_STRING = "Tarifs non communiqués";
    public static final int NO_DURATION = -1;

    public RepairService(long id,String firstName, String lastName, String location, boolean available, int duration, double distance,double latitude,double longitude,float rating,int price,String phoneNumber,int numberOfPeopleInqueue) {
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
        this.numberOfPeopleInqueue = numberOfPeopleInqueue;
    }

    public RepairService(long id, String firstName, String lastName, String location, String phoneNumber, boolean available, double latitude, double longitude, float rating, int price,int numberOfPeopleInqueue) {
        super(id, firstName, lastName);
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.available = available;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.price = price;
        this.numberOfPeopleInqueue = numberOfPeopleInqueue;
        distance = NO_DURATION;
        duration = NO_DURATION;
    }

    public RepairService (String firstname, String lastname)
    {
        super(firstname,lastname);
    }

    public RepairService(long id, String firstName, String lastName) {
        super(id, firstName, lastName);
    }

    public RepairService(long id, String firstName, String lastName, String location, String phoneNumber, float rating, int price) {
        super(id, firstName, lastName);
        this.location = location;
        this.phoneNumber = phoneNumber;
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
        if (duration!=NO_DURATION)
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
        return (s+"KM");}
        else
        {
            return (" ");
        }
    }
    public String getDurationString ()
    {
        double durationInMinutes = duration/60;
        NumberFormat nf = new DecimalFormat("0.#");
        String s = nf.format(durationInMinutes);
        return ("a "+s+" Minutes de route");

    }

    public String getAvailableString ()
    {
        if (available)
        {
            return "Disponible";

        }
        else
        {
            if (numberOfPeopleInqueue==0)
            {
                return "Occupé";
            }
            else
            {
                return numberOfPeopleInqueue+" Personnes en file d'attente";
            }
        }
    }
    public static ArrayList<RepairService> parseListJson(String jsonString)
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
                int numberOfPeopleInQueue = jsonObject.getInt("number_of_people_in_queue");

                RepairService repairService = new RepairService(id,firstname,lastname,location,available,duration,distance,latitude,longitude,rating,price,phoneNumber,numberOfPeopleInQueue);
                repairServices.add(repairService);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return repairServices;

        }
        return repairServices;

    }

    public static RepairService parseJson(String jsonString)
    {
        RepairService repairService = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            long id = jsonObject.getLong("id");
            String firstname = jsonObject.getString("first_name");
            String lastname =jsonObject.getString("last_name");
            String location = jsonObject.getString("location");
            String phoneNumber = jsonObject.getString("phone_number");
            int price = NO_PRICE;
            if (!jsonObject.isNull("price"))
            {
                price = jsonObject.getInt("price");
            }
            int av = jsonObject.getInt("available");
            boolean available = true;
            if (av ==0)
            {
                available=false;
            }
            float rating = (float)jsonObject.getDouble("rating");
            double longitude = jsonObject.getDouble("longitude");
            double latitude = jsonObject.getDouble("latitude");
            int numberOfPeopleInQueue = jsonObject.getInt("number_of_people_in_queue");
            repairService = new RepairService(id,firstname,lastname,location,phoneNumber,available,latitude,longitude,rating,price,numberOfPeopleInQueue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return repairService;
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




    public static void applyFiltre (ArrayList<RepairService> repairServices,OnlineFiltre onlineFiltre)
    {
        switch (onlineFiltre.getSortingMethod())
        {
            case OnlineFiltre.SORT_BY_DISTANCE : sortByDistance(repairServices);
                break;
            case OnlineFiltre.SORT_BY_PRICE : sortByPrice(repairServices);
                break;
            case OnlineFiltre.SORT_BY_RATING : sortByRating(repairServices);
        }
        filtrePrice(onlineFiltre.getMaxPrice(),repairServices);
        filtreRating(onlineFiltre.getMinRating(),repairServices);
        if (!onlineFiltre.isShowNotAvailable())
        {
            deleteNotAvailable(repairServices);
        }
    }
}

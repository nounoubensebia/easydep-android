package com.example.nouno.easydep.Data;

import java.util.ArrayList;

import static com.example.nouno.easydep.Data.OfflineFilter.SORT_BY_LOCATION;
import static com.example.nouno.easydep.Data.OfflineFilter.SORT_BY_PRICE;
import static com.example.nouno.easydep.Data.OfflineFilter.SORT_BY_RATING;

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

    public static void sortByLocation (ArrayList<OfflineRepairService> repairServices)
    {
        for (int i=0;i<repairServices.size();i++)
        {

            for (int j=i+1;j<repairServices.size();j++)
            {
                    int compare = repairServices.get(i).location.compareTo(repairServices.get(j).location);
                if (compare>0)
                {
                    OfflineRepairService repairService1 = repairServices.get(i);
                    OfflineRepairService repairService2 = repairServices.get(j);
                    repairServices.set(i,repairService2);
                    repairServices.set(j,repairService1);
                }
            }
        }
    }

    public static void sortByPrice (ArrayList<OfflineRepairService> repairServices)
    {
        for (int i=0;i<repairServices.size();i++)
        {

            for (int j=i+1;j<repairServices.size();j++)
            {

                if (repairServices.get(i).price>repairServices.get(j).price)
                {
                    OfflineRepairService repairService1 = repairServices.get(i);
                    OfflineRepairService repairService2 = repairServices.get(j);
                    repairServices.set(i,repairService2);
                    repairServices.set(j,repairService1);

                }
            }
        }

    }

    public static void filtrePrice (int price,ArrayList<OfflineRepairService> repairServices)
    {
        for (int i=0;i<repairServices.size();i++)
        {
            OfflineRepairService repairService = repairServices.get(i);

            if (price < repairService.price)
            {
                repairServices.remove(repairService);
                i--;
            }
        }

    }

    public static void sortByRating (ArrayList<OfflineRepairService> repairServices)
    {
        for (int i=0;i<repairServices.size();i++)
        {

            for (int j=i+1;j<repairServices.size();j++)
            {

                if (repairServices.get(i).rating<repairServices.get(j).rating)
                {
                    OfflineRepairService repairService1 = repairServices.get(i);
                    OfflineRepairService repairService2 = repairServices.get(j);
                    repairServices.set(i,repairService2);
                    repairServices.set(j,repairService1);

                }
            }
        }
    }

    public static void filtreRating (float rating,ArrayList<OfflineRepairService> repairServices)
    {
        //ArrayList<RepairService> availableRepairServices = new ArrayList<>();
        for (int i=0;i<repairServices.size();i++)
        {
            OfflineRepairService repairService = repairServices.get(i);
            if (rating > repairService.rating)
            {
                repairServices.remove(repairService);
                i--;
            }
        }
        //repairServices = new ArrayList<>(availableRepairServices);

    }


    public static void applyFilter (ArrayList<OfflineRepairService> repairServices,OfflineFilter offlineFilter)
    {
        switch (offlineFilter.getSortingMethod())
        {
            case SORT_BY_PRICE : sortByPrice(repairServices);
                break;
            case SORT_BY_LOCATION : sortByLocation(repairServices);
                break;
            case SORT_BY_RATING : sortByRating(repairServices);
                break;
        }

        if (offlineFilter.getMaxPrice()!= RepairService.NO_PRICE)
        filtrePrice(offlineFilter.getMaxPrice(),repairServices);
        filtreRating(offlineFilter.getMinRating(),repairServices);
    }

    public static ArrayList<OfflineRepairService> filterByInput (ArrayList<OfflineRepairService>repairServices,String input)
    {
        ArrayList<OfflineRepairService> filteredList = new ArrayList<OfflineRepairService>();
        for (OfflineRepairService offlineRepairService : repairServices)
        {
            if (offlineRepairService.getLocation().contains(input)||offlineRepairService.getLocation().toLowerCase().contains(input)||input.toLowerCase().contains(offlineRepairService.getLocation()))
            {
                filteredList.add(offlineRepairService);
            }
        }
        return filteredList;
    }
}

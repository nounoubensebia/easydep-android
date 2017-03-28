package com.example.nouno.easydep.Data;

/**
 * Created by nouno on 19/03/2017.
 */

public class OnlineFiltre extends OfflineFilter {
    public static final int SORT_BY_RATING = 0;
    public static final int SORT_BY_PRICE = 1;
    public static final int SORT_BY_DISTANCE = 2;
    public static final int MIN_RADIUS = 5;
    public static final int DEFAULT_RADIUS = 30;
    public static final int MAX_RADIUS = 50;
    public static final int MAX_PRICE = 1000;
    public static final int MIN_PRICE = 100;


    private boolean showNotAvailable;
    private int searchRadius;



    public OnlineFiltre(int sortingMethod, boolean showNotAvailable, int searchRadius, int maxPrice, int minRating) {
        super(sortingMethod,maxPrice,minRating);
        this.showNotAvailable = showNotAvailable;
        this.searchRadius = searchRadius;

    }

    public OnlineFiltre()
    {
        setSortingMethod(SORT_BY_DISTANCE);
        showNotAvailable = true;
        searchRadius = DEFAULT_RADIUS;
        setMaxPrice(RepairService.NO_PRICE);
        setMinRating(0);

    }



    public boolean isShowNotAvailable() {
        return showNotAvailable;
    }

    public int getSearchRadius() {
        return searchRadius;
    }







    public void setShowNotAvailable(boolean showNotAvailable) {
        this.showNotAvailable = showNotAvailable;
    }

    public void setSearchRadius(int searchRadius) {
        this.searchRadius = searchRadius;
    }




}

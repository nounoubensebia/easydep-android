package com.example.nouno.easydep;

/**
 * Created by nouno on 19/03/2017.
 */

public class Filtre {
    public static final int SORT_BY_RATING = 0;
    public static final int SORT_BY_PRICE = 1;
    public static final int SORT_BY_DISTANCE = 2;
    public static final int MIN_RADIUS = 5;
    public static final int DEFAULT_RADIUS = 30;
    public static final int MAX_RADIUS = 50;
    public static final int MAX_PRICE = 1000;
    public static final int MIN_PRICE = 100;

    private int sortingMethod;
    private boolean showNotAvailable;
    private int searchRadius;
    private int maxPrice;
    private float minRating;

    public Filtre(int sortingMethod, boolean showNotAvailable, int searchRadius, int maxPrice, float minRating) {
        this.sortingMethod = sortingMethod;
        this.showNotAvailable = showNotAvailable;
        this.searchRadius = searchRadius;
        this.maxPrice = maxPrice;
        this.minRating = minRating;
    }

    public Filtre ()
    {
        sortingMethod=SORT_BY_DISTANCE;
        showNotAvailable = true;
        searchRadius = DEFAULT_RADIUS;
        maxPrice = RepairService.NO_PRICE;
        minRating = 0;
    }

    public int getSortingMethod() {
        return sortingMethod;
    }

    public boolean isShowNotAvailable() {
        return showNotAvailable;
    }

    public int getSearchRadius() {
        return searchRadius;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public float getMinRating() {
        return minRating;
    }

    public void setSortingMethod(int sortingMethod) {
        this.sortingMethod = sortingMethod;
    }

    public void setShowNotAvailable(boolean showNotAvailable) {
        this.showNotAvailable = showNotAvailable;
    }

    public void setSearchRadius(int searchRadius) {
        this.searchRadius = searchRadius;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public void setMinRating(float minRating) {
        this.minRating = minRating;
    }
}

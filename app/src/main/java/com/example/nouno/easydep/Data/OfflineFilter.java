package com.example.nouno.easydep.Data;

import com.example.nouno.easydep.Data.RepairService;

/**
 * Created by nouno on 25/03/2017.
 */

public class OfflineFilter {
    private int sortingMethod;
    private int maxPrice;
    private int minRating;
    public static final int SORT_BY_PRICE = 0;
    public static final int SORT_BY_LOCATION = 1;
    public static final int SORT_BY_RATING = 2;

    public OfflineFilter(int sortingMethod, int maxPrice, int minRating) {
        this.sortingMethod = sortingMethod;
        this.maxPrice = maxPrice;
        this.minRating = minRating;
    }
    public OfflineFilter ()
    {
        sortingMethod = SORT_BY_LOCATION;
        maxPrice = RepairService.NO_PRICE;
        minRating = 0;
    }

    public int getSortingMethod() {
        return sortingMethod;
    }

    public void setSortingMethod(int sortingMethod) {
        this.sortingMethod = sortingMethod;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getMinRating() {
        return minRating;
    }

    public void setMinRating(int minRating) {
        this.minRating = minRating;
    }
}

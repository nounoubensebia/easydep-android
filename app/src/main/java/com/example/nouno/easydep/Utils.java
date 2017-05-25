package com.example.nouno.easydep;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.nouno.easydep.Data.CarOwner;
import com.example.nouno.easydep.Data.OfflineFilter;
import com.example.nouno.easydep.Data.OnlineFiltre;
import com.google.gson.Gson;

/**
 * Created by nouno on 27/03/2017.
 */

public class Utils {

    public static final String OFFLINEFILTER_KEY = "offlineFilter";
    public static final String ONLINEFILTER_KEY = "onlineFilter";


    public static void resetSettings (Context context)
    {
        resetFilters(context);

    }

    public static void resetFilters (Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        OnlineFiltre onlineFiltre = new OnlineFiltre();
        OfflineFilter offlineFilter = new OfflineFilter();
        String json = gson.toJson(onlineFiltre);
        editor.putString(ONLINEFILTER_KEY,json);
        json = gson.toJson(offlineFilter);
        editor.putString(OFFLINEFILTER_KEY,json);
        editor.commit();

    }

    public static  CarOwner getRegistredCarOwner (Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPref.getString("carOwner",null);
        CarOwner carOwner = gson.fromJson(json,CarOwner.class);
        return carOwner;
    }

    public static void saveCarOwner (CarOwner carOwner,Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(carOwner);
        editor.putString("carOwner",json);
        editor.commit();
    }



}

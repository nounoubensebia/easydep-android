package com.example.nouno.easydep.Data;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nouno on 23/03/2017.
 */

public class UserComment {
    private CarOwner carOwner;
    private RepairService repairService;
    private int rating;
    private String comment;
    private Date date;
    private boolean fromConnectedUser;
    private long id;


    public UserComment(long id,CarOwner carOwner,int rating, String comment, Date date, boolean fromConnectedUser) {
        this.id=id;
        this.carOwner = carOwner;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.fromConnectedUser = fromConnectedUser;
    }

    public UserComment(CarOwner carOwner, RepairService repairService, int rating) {
        this.carOwner = carOwner;
        this.repairService = repairService;
        this.rating = rating;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CarOwner getCarOwner() {
        return carOwner;
    }

    public RepairService getRepairService() {
        return repairService;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public Date getDate() {
        return date;
    }

    public boolean isFromConnectedUser() {
        return fromConnectedUser;
    }

    public static ArrayList<UserComment> parseJson (String json)
    {
        ArrayList<UserComment> userComments = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject carOwnerJson = jsonObject.getJSONObject("car_owner");
                String firstName = carOwnerJson.getString("first_name");
                String lastName = carOwnerJson.getString("last_name");
                long id = carOwnerJson.getLong("id");
                long commentId = jsonObject.getLong("id");
                CarOwner carOwner = new CarOwner(id,firstName,lastName,null,null);
                String commentText = jsonObject.getString("comment_text");
                long time = jsonObject.getLong("time");
                Date date = new Date(time*1000);
                boolean fromConnectedUser = jsonObject.getBoolean("from_connected_user");
                int rating = jsonObject.getInt("rating");
                userComments.add(new UserComment(commentId,carOwner,rating,commentText,date,fromConnectedUser));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sortByTime(userComments);
        return userComments;
    }

    public static boolean checkIfUserAlreadyCommented (ArrayList<UserComment> userComments)
    {
        for (int i=0;i<userComments.size();i++)
        {
            if (userComments.get(i).isFromConnectedUser())
                return true;
        }
        return false;
    }

    public static void sortByTime(ArrayList<UserComment> comments)
    {
        for (int i=0;i<comments.size();i++)
        {

            for (int j=i+1;j<comments.size();j++)
            {

                if (comments.get(i).date.getTime()<comments.get(j).date.getTime())
                {
                    UserComment comment1 = comments.get(i);
                    UserComment comment2 = comments.get(j);
                    comments.set(i,comment2);
                    comments.set(j,comment1);
                }
            }
        }

    }
    public String toJson ()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

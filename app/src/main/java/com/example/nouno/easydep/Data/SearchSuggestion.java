package com.example.nouno.easydep.Data;

import com.example.nouno.easydep.QueryUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by nouno on 20/03/2017.
 */

public class SearchSuggestion {
    private String primaryDescription;
    private String secondaryDescription;
    private String id;


    public SearchSuggestion(String primaryDescription, String secondaryDescription, String id) {
        this.primaryDescription = primaryDescription;
        this.secondaryDescription = secondaryDescription;
        this.id = id;
    }

    public String getPrimaryDescription() {
        return primaryDescription;
    }

    public String getSecondaryDescription() {
        return secondaryDescription;
    }

    public String getId() {
        return id;
    }

    public static ArrayList<SearchSuggestion> parseJson (String json)
    {
        ArrayList<SearchSuggestion> arrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("predictions");
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject predObject = jsonArray.getJSONObject(i);
                String id = predObject.getString("place_id");
                JSONObject structuredFormatting = predObject.getJSONObject("structured_formatting");
                JSONArray types = predObject.getJSONArray("types");
                String type = types.getString(0);
                if (!type.equals("country") )
                {
                    String mainText = structuredFormatting.getString("main_text");
                    String secondaryText = structuredFormatting.getString("secondary_text");
                    String[] tab = secondaryText.split(", Algérie");
                    arrayList.add(new SearchSuggestion(mainText,tab[0],id));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public Position getPosition()
    {
        Position position = null;
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("placeid",id);
        map.put("key","AIzaSyAqQHxLWPTvFHDvz5WUwuNAjTa0UuSHbmk");
        String jsonResponse = QueryUtils.makeHttpGetRequest(QueryUtils.GET_PLACE_POSITION_URL,map);
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject result = jsonObject.getJSONObject("result");
            JSONObject geometry = result.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            position = new Position(primaryDescription+", "+secondaryDescription,location.getDouble("lat"),location.getDouble("lng"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return position;
    }
}

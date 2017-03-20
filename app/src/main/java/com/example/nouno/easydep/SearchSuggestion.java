package com.example.nouno.easydep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
                String id = predObject.getString("id");
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
}

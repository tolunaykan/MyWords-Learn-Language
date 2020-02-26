package com.tolunaykandirmaz.mywords.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser {
        
    private JSONObject jsonObject;
    
    public JSONParser(JSONObject jsonObject){
        this.jsonObject = jsonObject;    
    }
    
    public ArrayList<String> getLinks() throws JSONException {
        ArrayList<String> results = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("hits");
        for (int i=0; i<jsonArray.length(); i++){
            results.add(jsonArray.getJSONObject(i).getString("webformatURL"));
        }
        return results;
    }
}

package com.bosSearch.bossearch;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramSearchLocationsRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramLocation;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchLocationsResult;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static org.brunocvcunha.instagram4j.InstagramConstants.API_KEY;

public class Utils {
    public static InstagramLocation getCityEqualsTo(String city) throws IOException, JSONException {
        Instagram4j instagram = Instagram4j.builder().username("boss.earch").password("Asd101196").build();
        instagram.setup();
        instagram.login();

        String[] coordinates = getLatLng(city).split(" ");

        System.out.println(coordinates[0] + " " + coordinates[1]);

        InstagramSearchLocationsResult locationsResult = instagram.sendRequest(
                new InstagramSearchLocationsRequest("52.237049","21.017532", city));


        for(InstagramLocation location : locationsResult.getVenues()) {
            if(location.getName().equals(city)){
                return location;
            }
        }

        return null;
    }

//    public static InstagramFeedResult getPhotoPack(InstagramLocation location){
//
//    }

    private static String getLatLng(String city) throws IOException, JSONException {
        String surl = "https://maps.googleapis.com/maps/api/geocode/json?address="+ URLEncoder.encode(city, "UTF-8")+"&key="+API_KEY;
        URL url = new URL(surl);
        InputStream is = url.openConnection().getInputStream();

        BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);

        JSONObject jo = new JSONObject(responseStrBuilder.toString());
        JSONArray results = jo.getJSONArray("results");
        String lat = null;
        String lng = null;
        String region = null;
        String province = null;
        String zip = null;
        Map<String, String> ret = new HashMap<String, String>();
        if(results.length() > 0) {
            JSONObject jsonObject;
            jsonObject = results.getJSONObject(0);
            ret.put("lat", jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat"));
            ret.put("lng", jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng"));
        }
        String coordinates = ret.get("lat") + " " + ret.get("lng");
        return coordinates;
    }
}

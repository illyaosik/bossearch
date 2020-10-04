package com.bosSearch.bossearch.Services;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramSearchLocationsRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramLocation;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchLocationsResult;

import java.io.IOException;

public class FindFyGeolocation {
    private Instagram4j instagram4j;

    public FindFyGeolocation(Instagram4j instagram4j) {
        this.instagram4j = instagram4j;
    }

    public void findByCity(String city) throws IOException {
        InstagramSearchLocationsResult locationsRequest = instagram4j.sendRequest(
                new InstagramSearchLocationsRequest("52.237049","21.017532",""));
        for(InstagramLocation location : locationsRequest.getVenues()){
            System.out.println(location.getName());
        }
    }
}

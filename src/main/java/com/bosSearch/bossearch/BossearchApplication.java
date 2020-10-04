package com.bosSearch.bossearch;

import com.bosSearch.bossearch.Services.FindFyGeolocation;
import com.bosSearch.bossearch.Services.InstagramScrapper;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetChallengeRequest;
import org.brunocvcunha.instagram4j.requests.InstagramLocationFeedRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchLocationsRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedItem;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramLocation;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchLocationsResult;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.io.IOException;

@SpringBootApplication
public class BossearchApplication {

	public static void main(String[] args) throws IOException, JSONException {
		SpringApplication.run(BossearchApplication.class, args);
		Instagram4j instagram = Instagram4j.builder().username("boss.earch").password("Asd101196").build();
		instagram.setup();
		instagram.login();

		InstagramLocation location = Utils.getCityEqualsTo("Warsaw");
		if (location != null) {
			System.out.println(location.getName());




			InstagramFeedResult result1;
			result1 = instagram.sendRequest(new InstagramLocationFeedRequest(location.getExternal_id()));

			System.out.println(result1.getNext_max_id());
			System.out.println(result1.getNum_results());
			System.out.println(result1.getItems().get(0).getCaption().getText());

			result1 = instagram.sendRequest(new InstagramLocationFeedRequest(location.getExternal_id(),result1.getNext_max_id().toString()));

			System.out.println(result1.getNext_max_id());
			System.out.println(result1.getNum_results());
			System.out.println(result1.getItems().get(0).getCaption().getText());

			result1 = instagram.sendRequest(new InstagramLocationFeedRequest(location.getExternal_id(),result1.getNext_max_id().toString()));

			System.out.println(result1.getNext_max_id());
			System.out.println(result1.getNum_results());

			result1 = instagram.sendRequest(new InstagramLocationFeedRequest(location.getExternal_id(),result1.getNext_max_id().toString()));

			System.out.println(result1.getNext_max_id());
			System.out.println(result1.getNum_results());
		}
	}

}

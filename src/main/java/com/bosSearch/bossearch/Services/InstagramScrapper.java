package com.bosSearch.bossearch.Services;

import com.bosSearch.bossearch.Model.Gender;
import com.bosSearch.bossearch.Model.Language;
import com.bosSearch.bossearch.Model.Person;
import com.bosSearch.bossearch.Repository.PersonRepo;
import com.bosSearch.bossearch.Services.Utils.Utils;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedItem;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramLocation;
import org.brunocvcunha.instagram4j.requests.payload.InstagramLoginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class InstagramScrapper {
    private InstagramLocation location;
    private InstagramFeedResult feedResult;
    private int counterIteration = 0;
    private int counterFail = 0;

    @Autowired
    private ServicePerson servicePerson;

    @Autowired
    private PersonRepo personRepo;

    public void start(String locationToFind) throws IOException, JSONException, InterruptedException {
        preparing(locationToFind);

        for (int i = 0; i < Accounts.accounts.size(); i++) {
            login(i);
            do { //login every time - bad
                Map<String, Object> receivedResult = Utils.getPostsPack(location, i);
                feedResult = (InstagramFeedResult) receivedResult.get("instagramFeedResult");
                if (feedResult == null || feedResult.getMessage() != null) { //nullpointer ->  || feedResult.getMessage().equals("") ???
                    if (counterFail >= 1) break;
                    try {
                        counterFail++;
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (feedResult.getNum_results() < 1) {
                    System.out.println(feedResult.getNum_results() + "Results are empty!");
                } else if (feedResult != null) {
                    System.out.println("\n" + feedResult.getItems().size() + " - number of results \n");
                    Arrays.filteredByLocation.addAll(feedResult.getItems());
                    //String asa = Arrays.filteredByLocation.get(0).image_versions2.candidates.get(0).url;
                    counterIteration++;
                    counterFail = 0;
                    if (counterIteration == 10) {
                        counterIteration = 0;
                        CompletableFuture.runAsync(this::processPack);
                    }
                }
            } while (feedResult != null);
        }
        System.out.println("More posts are not available");
    }

    private void preparing(String locationToFind) throws IOException, JSONException {
        Accounts.accounts = (ArrayList<Instagram4j>) Utils.readBots("/Users/illiaosiyuk/Desktop/bossearch/src/main/resources/static/Bots.txt"); //bots accounts
        location = Utils.getCityEqualsTo(locationToFind); //locationToFind
        Accounts.accountsForDownloading = (ArrayList<Instagram4j>) Utils.readBots("/Users/illiaosiyuk/Desktop/bossearch/src/main/resources/static/BotsToTakePhotos.txt");
        login(Accounts.accountsForDownloading.get(0));
        feedResult = null;
        Arrays.alreadyAdded = (ArrayList<String>) personRepo.findUsernames();
    }

    private boolean login(int index) throws IOException {
        Accounts.accounts.get(index).setup();
        return !Accounts.accounts.get(index).login().getStatus().equals("fail");
    }

    public static boolean login(Instagram4j account) throws IOException {
        account.setup();
        InstagramLoginResult result = account.login();
        return !result.getStatus().equals("fail");
    }

    private boolean isUsernameAdded(String username) {
        if (!Arrays.alreadyAdded.contains(username)) {
            Arrays.alreadyAdded.add(username);
            return false;
        }
        return true;
    }

    private void addFilteredUser(InstagramFeedItem post) {
        if (!isUsernameAdded(post.caption.getUser().username)) {
            Person p = new Person(post.caption.getUser().username, Language.RUSSIAN, Gender.UNKNOWN);
            Utils.takeAndSavePhotos(post.getUser());
            servicePerson.savePerson(p);
            personRepo.flush();
            System.out.println("Added to DB after filtering: " + post.caption.getUser().username);
        }
    }

    private static int logCounter = 0;

    void processPack() {
        ArrayList<InstagramFeedItem> posts = new ArrayList<>(Arrays.filteredByLocation);
        Arrays.filteredByLocation.clear();
        for (InstagramFeedItem post : posts) {
            logCounter++;
            System.out.println("Post number # " + logCounter);
            if (post.caption != null && Utils.takeByLanguage(post.caption.getText())) {
                addFilteredUser(post);
                continue;
            }
            for (int i = 0; i < post.getPreview_comments().size(); i++) {
                if (post.caption != null && Utils.takeByLanguage(post.getPreview_comments().get(i).getText())) {
                    addFilteredUser(post);
                }
            }
        }
        posts.clear();
    }
}

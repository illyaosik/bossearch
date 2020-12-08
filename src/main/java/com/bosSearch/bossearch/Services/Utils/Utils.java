package com.bosSearch.bossearch.Services.Utils;

import com.bosSearch.bossearch.Services.Accounts;
import com.bosSearch.bossearch.Services.InstagramScrapper;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramLocationFeedRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchLocationsRequest;
import org.brunocvcunha.instagram4j.requests.InstagramUserFeedRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramLocation;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchLocationsResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    public static InstagramLocation getCityEqualsTo(String locationToSearch) throws IOException, JSONException {
        String[] coordinates = getLatLng(locationToSearch).split(" ");

        System.out.println(coordinates[0] + " " + coordinates[1]);

        InstagramSearchLocationsResult locationsResult;

        for (Instagram4j account : Accounts.accounts) {
            if (InstagramScrapper.login(account)) {
                locationsResult = account.sendRequest(
                        new InstagramSearchLocationsRequest(coordinates[0], coordinates[1], locationToSearch));
                locationToSearch = locationToSearch.replace('_', ' ');
                for (InstagramLocation location : locationsResult.getVenues()) {
                    if (location.getName().equals(locationToSearch)) {
                        System.out.println("Location found!" + "\n");
                        return location;
                    }
                }
            }
        }
        return null;
    }

    private static InstagramFeedResult feedResult;

    public static Map<String, Object> getPostsPack(InstagramLocation location, int index) throws IOException {
        Map<String, Object> result = new HashMap<>();

        if (feedResult != null && location != null) {
            System.out.println("Location: " + location.getName() + " - checking for posts...");
            feedResult = Accounts.accounts.get(index).sendRequest(new InstagramLocationFeedRequest(location.getExternal_id(), feedResult.getNext_max_id()));
        } else if (feedResult == null && location != null) {
            System.out.println("Location: " + location.getName() + " - FIRST checking for posts...");
            feedResult = Accounts.accounts.get(index).sendRequest(new InstagramLocationFeedRequest(location.getExternal_id()));
        }

        result.put("instagramLocation", location);
        result.put("instagramFeedResult", feedResult);
        return result;
    }

    public static boolean takeByLanguage(String text) {
        System.out.println("checking....");
        for (int i = 0; i < text.length(); i++) {
            if (Character.UnicodeBlock.of(text.charAt(i)).equals(Character.UnicodeBlock.CYRILLIC)) {
                return true;
            }
        }
        return false;
    }

    @Value("${google.api.key}")
    private static String googleApiKey;

    private static String getLatLng(String location) throws IOException, JSONException {
        String surl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(location, "UTF-8") + "&key=" + googleApiKey;
        URL url = new URL(surl);
        InputStream is = url.openConnection().getInputStream();

        BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);

        JSONObject jo = new JSONObject(responseStrBuilder.toString());
        JSONArray results = jo.getJSONArray("results");
        Map<String, String> ret = new HashMap<String, String>();
        if (results.length() > 0) {
            JSONObject jsonObject;
            jsonObject = results.getJSONObject(0);
            ret.put("lat", jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat"));
            ret.put("lng", jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng"));
        }
        //System.out.println(coordinates + " coordinates of place");
        return ret.get("lat") + " " + ret.get("lng");
    }

    public static List<Instagram4j> readBots(String path) {
        try {
            return Files.lines(Paths.get(path))
                    .map(l -> new Instagram4j(l.split(" - ")[0], l.split(" - ")[1]))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Value("${photo.folder}")
    private static String photoFolderPath;

    public static void takeAndSavePhotos(InstagramUser instagramUser) {
        try {
            InstagramFeedResult feedResult = Accounts.accountsForDownloading.get(0).sendRequest(new InstagramUserFeedRequest(instagramUser.pk));
            Path usernamePath = Paths.get(photoFolderPath + feedResult.getItems().get(0).user.username);
            Files.createDirectories(usernamePath);
            for (int i = 0; i < feedResult.getItems().size(); i++) {
                if (feedResult.getItems().get(i).getCarousel_media() != null) {
                    if (feedResult.getItems().get(i).getCarousel_media().get(0).getVideo_versions() != null) {
                        System.out.println("This is video");
                        saveFeedItem(feedResult.getItems().get(i).getCarousel_media().get(0).getVideo_versions().get(0).url, feedResult.getItems().get(i).pk, usernamePath, "mp4");
                    } else {
                        saveFeedItem(feedResult.getItems().get(i).getCarousel_media().get(0).getImage_versions2().candidates.get(0).url, feedResult.getItems().get(i).pk, usernamePath, "jpg");
                    }
                } else {
                    if (feedResult.getItems().get(i).getVideo_versions() != null) {
                        saveFeedItem(feedResult.getItems().get(i).getVideo_versions().get(0).url, feedResult.getItems().get(i).pk, usernamePath, "mp4");
                    } else {
                        saveFeedItem(feedResult.getItems().get(i).getImage_versions2().candidates.get(0).url, feedResult.getItems().get(i).pk, usernamePath, "jpg");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveFeedItem(String url, long pk, Path path, String format) {
        InputStream inputStream;
        try {
            inputStream = new URL(url).openStream();
            File file = new File(path + "/" + pk + '.' + format);
            if (format.equals("jpg")) {
                BufferedImage bf = ImageIO.read(inputStream);
                ImageIO.write(bf, format, file);
            } else if (format.equals("mp4")) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new URL(url).openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(path + "/" + pk + '.' + format);
                int count;
                byte[] b = new byte[100];
                while ((count = bufferedInputStream.read(b)) != -1) {
                    fileOutputStream.write(b, 0, count);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

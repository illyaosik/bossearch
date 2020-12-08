package com.bosSearch.bossearch.Services;

import org.brunocvcunha.instagram4j.Instagram4j;

import java.util.ArrayList;

public class Accounts {
    //below should be accounts from bots.txt to parse all photos
    public static ArrayList<Instagram4j> accounts = new ArrayList<>();
    //below should be accounts from botsToTakePhotos.txt for possibility of images/videos downloading
    public static ArrayList<Instagram4j> accountsForDownloading = new ArrayList<>();
}

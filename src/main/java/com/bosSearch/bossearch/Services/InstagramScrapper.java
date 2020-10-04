package com.bosSearch.bossearch.Services;

import org.brunocvcunha.instagram4j.Instagram4j;

import java.io.IOException;

public class InstagramScrapper {

    public static Instagram4j instagram4j = null;

    public static void initializeInstargamScrapper() throws IOException {
        instagram4j = Instagram4j.builder().username("boss.earch").password("Asd101196").build();
    }
}

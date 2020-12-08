package com.bosSearch.bossearch;

import com.bosSearch.bossearch.Services.InstagramScrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class BossearchApplication {


    public static void main(String[] args) {
        SpringApplication.run(BossearchApplication.class, args);
    }

    @Component
    public class CommandLineAppStartupRunner implements CommandLineRunner {
        @Autowired
        private InstagramScrapper instagramScrapper;

        @Override
        public void run(String... args) throws Exception {
            instagramScrapper.start("Warsaw"); // spaces should be changed with '_'
        }
    }
}

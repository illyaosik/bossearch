package com.bosSearch.bossearch.Model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class Person {
    @Id
    private long id;
    private String username;
    private Language language;
    private Gender gender;

    public Person() {
    }

    public Person(String username, Language language, Gender gender) {
        this.username = username;
        this.language = language;
        this.gender = gender;
    }

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Language getLanguage() {
        return language;
    }

    @Enumerated(EnumType.STRING)
    public void setLanguage(Language language) {
        this.language = language;
    }

    @Enumerated(EnumType.STRING)
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}

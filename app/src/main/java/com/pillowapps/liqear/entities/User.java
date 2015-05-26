package com.pillowapps.liqear.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    private enum Gender {
        MALE, FEMALE, UNKNOWN
    }

    @SerializedName("name")
    private String name = "";
    private String country;
    private Integer age;
    private long playcount;
    private Gender gender;
    private double match;
    private long uid;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public User(String name) {
        super();
        this.name = name;
    }

    public User(String name, long uid) {
        this.name = name;
        this.uid = uid;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public boolean hasName() {
        return !name.equals("");
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public long getPlaycount() {
        return playcount;
    }

    public void setPlaycount(long playcount) {
        this.playcount = playcount;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public double getMatch() {
        return match;
    }

    public void setMatch(double match) {
        this.match = match;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

}

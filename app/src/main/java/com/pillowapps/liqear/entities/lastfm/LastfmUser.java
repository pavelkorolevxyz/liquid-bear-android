package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmUser {
    @SerializedName("name")
    String name;
    @SerializedName("image")
    List<LastfmImage> images;
    @SerializedName("match")
    double match;

    public LastfmUser() {
    }

    public String getName() {
        return name;
    }

    public List<LastfmImage> getImages() {
        return images;
    }

    public double getMatch() {
        return match;
    }

    @Override
    public String toString() {
        return "LastfmUser{" +
                "name='" + name + '\'' +
                ", images=" + images +
                '}';
    }
}

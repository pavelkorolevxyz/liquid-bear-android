package com.pillowapps.liqear.models.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmUser {
    @SerializedName("name")
    String name;
    @SerializedName("image")
    List<LastfmImage> images;

    public LastfmUser() {
    }

    public String getName() {
        return name;
    }

    public List<LastfmImage> getImages() {
        return images;
    }

    @Override
    public String toString() {
        return "LastfmUser{" +
                "name='" + name + '\'' +
                ", images=" + images +
                '}';
    }
}

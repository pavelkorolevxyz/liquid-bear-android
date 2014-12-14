package com.pillowapps.liqear.models.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmArtist {

    @SerializedName("name")
    String name;
    @SerializedName("image")
    List<LastfmImage> images;
    @SerializedName("bio")
    private LastfmArtistBio bio;

    public LastfmArtist() {
    }

    public String getName() {
        return name;
    }

    public LastfmArtistBio getBio() {
        return bio;
    }

    public List<LastfmImage> getImages() {
        return images;
    }

    @Override
    public String toString() {
        return "LastfmArtist{" +
                "name='" + name + '\'' +
                ", images=" + images +
                ", bio=" + bio +
                '}';
    }
}

package com.pillowapps.liqear.entities.lastfm;

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

    public void setName(String name) {
        this.name = name;
    }

    public void setImages(List<LastfmImage> images) {
        this.images = images;
    }

    public void setBio(LastfmArtistBio bio) {
        this.bio = bio;
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

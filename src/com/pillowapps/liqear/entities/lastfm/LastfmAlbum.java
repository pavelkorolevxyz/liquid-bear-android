package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmAlbum {

    @SerializedName("name")
    String name;
    @SerializedName("artist")
    String artist;
    @SerializedName("releaseDate")
    String releaseDate;
    @SerializedName("image")
    List<LastfmImage> images;
    @SerializedName("tracks")
    LastfmTracks tracks;

    public LastfmAlbum() {
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public List<LastfmImage> getImages() {
        return images;
    }

    public LastfmTracks getTracks() {
        return tracks;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String toString() {
        return "LastfmAlbum{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", releaseDate=" + releaseDate +
                ", images=" + images +
                ", tracks=" + tracks +
                '}';
    }
}

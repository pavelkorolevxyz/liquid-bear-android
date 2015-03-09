package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmAlbum {

    @SerializedName("title")
    private String title;
    @SerializedName("artist")
    private String artist;
    @SerializedName("releaseDate")
    private String releaseDate;
    @SerializedName("image")
    private List<LastfmImage> images;
    @SerializedName("tracks")
    private LastfmTracks tracks;

    public LastfmAlbum() {
    }

    public String getTitle() {
        return title;
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
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", releaseDate=" + releaseDate +
                ", images=" + images +
                ", tracks=" + tracks +
                '}';
    }
}

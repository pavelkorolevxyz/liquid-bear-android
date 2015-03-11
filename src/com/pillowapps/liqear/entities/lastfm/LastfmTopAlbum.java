package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmTopAlbum {

    @SerializedName("name")
    private String name;
    @SerializedName("artist")
    private LastfmArtist artist;
    @SerializedName("releaseDate")
    private String releaseDate;
    @SerializedName("image")
    private List<LastfmImage> images;
    @SerializedName("tracks")
    private LastfmTracks tracks;

    public LastfmTopAlbum() {
    }

    public String getName() {
        return name;
    }

    public LastfmArtist getArtist() {
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

}

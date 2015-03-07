package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmTrack {

    @SerializedName("name")
    String name;
    @SerializedName("artist")
    LastfmArtist artist;

    public LastfmTrack() {
    }

    public String getName() {
        return name;
    }

    public LastfmArtist getArtist() {
        return artist;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(LastfmArtist artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "LastfmTrack{" +
                "name='" + name + '\'' +
                ", artist=" + artist +
                '}';
    }
}


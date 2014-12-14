package com.pillowapps.liqear.models.lastfm;

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

    @Override
    public String toString() {
        return "LastfmTrack{" +
                "name='" + name + '\'' +
                ", artist=" + artist +
                '}';
    }
}


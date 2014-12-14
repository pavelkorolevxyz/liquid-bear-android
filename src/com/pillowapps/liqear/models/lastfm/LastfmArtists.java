package com.pillowapps.liqear.models.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmArtists {
    @SerializedName("artist")
    List<LastfmArtist> artists;

    public LastfmArtists() {
    }

    public List<LastfmArtist> getArtists() {
        return artists;
    }

    @Override
    public String toString() {
        return "LastfmArtists{" +
                "artists=" + artists +
                '}';
    }
}

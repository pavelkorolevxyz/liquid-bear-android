package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmArtistSearchResults {
    @SerializedName("artistmatches")
    LastfmArtists artists;

    public LastfmArtistSearchResults() {
    }

    public LastfmArtists getArtists() {
        return artists;
    }

    @Override
    public String toString() {
        return "LastfmSearchResults{" +
                "artists=" + artists +
                '}';
    }
}

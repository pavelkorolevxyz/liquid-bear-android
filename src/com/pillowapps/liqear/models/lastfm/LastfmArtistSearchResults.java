package com.pillowapps.liqear.models.lastfm;

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

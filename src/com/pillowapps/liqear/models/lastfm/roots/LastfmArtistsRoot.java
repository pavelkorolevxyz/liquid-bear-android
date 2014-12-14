package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmArtists;

public class LastfmArtistsRoot {

    @SerializedName("artists")
    LastfmArtists artists;

    public LastfmArtistsRoot() {
    }

    public LastfmArtists getArtists() {
        return artists;
    }

    @Override
    public String toString() {
        return "LastfmSimilarArtistsRoot{" +
                "artists=" + artists +
                '}';
    }
}

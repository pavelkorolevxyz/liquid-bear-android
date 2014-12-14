package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmArtists;

public class LastfmSimilarArtistsRoot {

    @SerializedName("similarartists")
    LastfmArtists artists;

    public LastfmSimilarArtistsRoot() {
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

package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmArtists;

public class LastfmSimilarArtistsRoot extends LastfmResponse {

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

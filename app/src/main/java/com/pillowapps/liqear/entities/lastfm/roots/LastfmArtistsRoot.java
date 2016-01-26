package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmArtists;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;

public class LastfmArtistsRoot extends LastfmResponse {

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

package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmArtists;

public class LastfmTopArtistsRoot extends LastfmResponse {
    @SerializedName("topartists")
    LastfmArtists artists;

    public LastfmTopArtistsRoot() {
    }

    public LastfmArtists getArtists() {
        return artists;
    }

    @Override
    public String toString() {
        return "LastfmTopArtistsRoot{" +
                "artists=" + artists +
                '}';
    }
}

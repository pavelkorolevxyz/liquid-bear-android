package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmArtists;

public class LastfmTopArtistsRoot {
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

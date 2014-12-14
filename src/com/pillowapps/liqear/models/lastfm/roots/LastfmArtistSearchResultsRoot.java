package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmArtistSearchResults;

public class LastfmArtistSearchResultsRoot {
    @SerializedName("results")
    LastfmArtistSearchResults results;

    public LastfmArtistSearchResultsRoot() {
    }

    public LastfmArtistSearchResults getResults() {
        return results;
    }

    @Override
    public String toString() {
        return "LastfmSearchResultsRoot{" +
                "results=" + results +
                '}';
    }
}

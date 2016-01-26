package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmArtistSearchResults;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;

public class LastfmArtistSearchResultsRoot extends LastfmResponse {
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

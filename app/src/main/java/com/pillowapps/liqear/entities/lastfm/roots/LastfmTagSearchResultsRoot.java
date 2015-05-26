package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmTagSearchResults;

public class LastfmTagSearchResultsRoot extends LastfmResponse {
    @SerializedName("results")
    LastfmTagSearchResults results;

    public LastfmTagSearchResultsRoot() {
    }

    public LastfmTagSearchResults getResults() {
        return results;
    }

    @Override
    public String toString() {
        return "LastfmSearchResultsRoot{" +
                "results=" + results +
                '}';
    }
}

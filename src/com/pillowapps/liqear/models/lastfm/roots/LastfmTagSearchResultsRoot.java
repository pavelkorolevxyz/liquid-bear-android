package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmArtistSearchResults;
import com.pillowapps.liqear.models.lastfm.LastfmTagSearchResults;

public class LastfmTagSearchResultsRoot {
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

package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmAlbumSearchResults;
import com.pillowapps.liqear.models.lastfm.LastfmTagSearchResults;

public class LastfmAlbumSearchResultsRoot {
    @SerializedName("results")
    LastfmAlbumSearchResults results;

    public LastfmAlbumSearchResultsRoot() {
    }

    public LastfmAlbumSearchResults getResults() {
        return results;
    }

    @Override
    public String toString() {
        return "LastfmSearchResultsRoot{" +
                "results=" + results +
                '}';
    }
}

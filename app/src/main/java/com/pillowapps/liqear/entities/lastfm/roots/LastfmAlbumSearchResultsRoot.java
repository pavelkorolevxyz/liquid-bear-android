package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbumSearchResults;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;

public class LastfmAlbumSearchResultsRoot extends LastfmResponse {
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

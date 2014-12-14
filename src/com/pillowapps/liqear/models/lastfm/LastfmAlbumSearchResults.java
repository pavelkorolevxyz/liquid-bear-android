package com.pillowapps.liqear.models.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmAlbumSearchResults {
    @SerializedName("albummatches")
    LastfmAlbums albums;

    public LastfmAlbumSearchResults() {
    }

    public LastfmAlbums getAlbums() {
        return albums;
    }

    @Override
    public String toString() {
        return "LastfmAlbumSearchResults{" +
                "albums=" + albums +
                '}';
    }
}

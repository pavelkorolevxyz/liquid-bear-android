package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmAlbums;

public class LastfmTopAlbumsRoot {

    @SerializedName("topAlbums")
    LastfmAlbums albums;

    public LastfmTopAlbumsRoot() {
    }

    public LastfmAlbums getAlbums() {
        return albums;
    }

    @Override
    public String toString() {
        return "LastfmAlbumsRoot{" +
                "albums=" + albums +
                '}';
    }
}

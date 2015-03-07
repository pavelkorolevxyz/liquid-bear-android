package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbums;

public class LastfmTopAlbumsRoot extends LastfmResponse {

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

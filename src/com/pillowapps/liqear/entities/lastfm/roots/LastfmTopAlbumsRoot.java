package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbums;
import com.pillowapps.liqear.entities.lastfm.LastfmTopAlbums;

public class LastfmTopAlbumsRoot extends LastfmResponse {

    @SerializedName("topalbums")
    LastfmTopAlbums albums;

    public LastfmTopAlbumsRoot() {
    }

    public LastfmTopAlbums getAlbums() {
        return albums;
    }

    @Override
    public String toString() {
        return "LastfmAlbumsRoot{" +
                "albums=" + albums +
                '}';
    }
}

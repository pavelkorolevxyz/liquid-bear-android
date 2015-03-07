package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmAlbums {
    @SerializedName("album")
    List<LastfmAlbum> albums;

    public LastfmAlbums() {
    }

    public List<LastfmAlbum> getAlbums() {
        return albums;
    }

    @Override
    public String toString() {
        return "LastfmAlbums{" +
                "albums=" + albums +
                '}';
    }
}

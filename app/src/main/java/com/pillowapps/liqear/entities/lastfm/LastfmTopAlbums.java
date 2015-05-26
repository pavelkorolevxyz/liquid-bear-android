package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmTopAlbums {
    @SerializedName("album")
    List<LastfmTopAlbum> albums;

    public LastfmTopAlbums() {
    }

    public List<LastfmTopAlbum> getAlbums() {
        return albums;
    }

    @Override
    public String toString() {
        return "LastfmAlbums{" +
                "albums=" + albums +
                '}';
    }
}

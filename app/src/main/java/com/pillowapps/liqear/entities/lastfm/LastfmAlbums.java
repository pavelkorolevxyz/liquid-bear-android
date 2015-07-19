package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LastfmAlbums {
    @SerializedName("album")
    List<LastfmAlbum> albums;

    public LastfmAlbums() {
        albums = new ArrayList<>();
    }

    public List<LastfmAlbum> getAlbums() {
        return albums;
    }

    public void setAlbums(List<LastfmAlbum> albums) {
        this.albums = albums;
    }

    @Override
    public String toString() {
        return "LastfmAlbums{" +
                "albums=" + albums +
                '}';
    }
}

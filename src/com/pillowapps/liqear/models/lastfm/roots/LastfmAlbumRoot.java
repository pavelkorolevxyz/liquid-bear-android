package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmAlbum;

public class LastfmAlbumRoot {

    @SerializedName("album")
    LastfmAlbum album;

    public LastfmAlbumRoot() {
    }

    public LastfmAlbum getAlbum() {
        return album;
    }

    @Override
    public String toString() {
        return "LastfmAlbumRoot{" +
                "albums=" + album +
                '}';
    }
}

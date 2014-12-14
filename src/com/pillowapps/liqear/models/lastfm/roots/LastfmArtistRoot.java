package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmArtist;

public class LastfmArtistRoot {
    @SerializedName("artist")
    LastfmArtist artist;

    public LastfmArtistRoot() {
    }

    public LastfmArtist getArtist() {
        return artist;
    }

    @Override
    public String toString() {
        return "LastfmArtistRoot{" +
                "artist=" + artist +
                '}';
    }
}

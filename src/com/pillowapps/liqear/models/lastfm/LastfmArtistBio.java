package com.pillowapps.liqear.models.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmArtistBio {
    @SerializedName("content")
    String content;

    public LastfmArtistBio() {
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "LastfmArtistBio{" +
                "content='" + content + '\'' +
                '}';
    }
}

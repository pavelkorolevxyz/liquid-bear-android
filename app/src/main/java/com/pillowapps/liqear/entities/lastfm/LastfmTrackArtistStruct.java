package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmTrackArtistStruct {

    @SerializedName("name")
    String name;
    @SerializedName("artist")
    LastfmArtistStruct artist;

    public LastfmTrackArtistStruct() {
    }

    public String getName() {
        return name;
    }

    public LastfmArtistStruct getArtist() {
        return artist;
    }

    @Override
    public String toString() {
        return "LastfmTrack{" +
                "name='" + name + '\'' +
                ", artist=" + artist +
                '}';
    }
}


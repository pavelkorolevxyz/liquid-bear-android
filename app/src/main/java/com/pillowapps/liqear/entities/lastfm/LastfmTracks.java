package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmTracks {
    @SerializedName("track")
    List<LastfmTrack> tracks;

    public LastfmTracks() {
    }

    public List<LastfmTrack> getTracks() {
        return tracks;
    }

    @Override
    public String toString() {
        return "LastfmTracks{" +
                "tracks=" + tracks +
                '}';
    }
}

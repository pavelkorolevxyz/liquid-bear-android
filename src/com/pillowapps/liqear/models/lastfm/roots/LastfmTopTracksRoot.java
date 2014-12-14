package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmTracks;

public class LastfmTopTracksRoot {

    @SerializedName("toptracks")
    LastfmTracks tracks;

    public LastfmTopTracksRoot() {
    }

    public LastfmTracks getTracks() {
        return tracks;
    }

    @Override
    public String toString() {
        return "LastfmTopTracksRoot{" +
                "tracks=" + tracks +
                '}';
    }
}

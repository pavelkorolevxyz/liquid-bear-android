package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmTracks;

public class LastfmTopTracksRoot extends LastfmResponse {

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

package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmTracks;

public class LastfmLovedTracksRoot extends LastfmResponse {

    @SerializedName("lovedtracks")
    LastfmTracks tracks;

    public LastfmLovedTracksRoot() {
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

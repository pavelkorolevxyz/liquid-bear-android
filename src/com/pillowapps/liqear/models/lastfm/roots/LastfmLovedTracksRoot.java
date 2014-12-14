package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmTracks;

public class LastfmLovedTracksRoot {

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

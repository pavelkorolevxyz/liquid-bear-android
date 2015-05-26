package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmTracks;

public class LastfmTracksRoot extends LastfmResponse {
    @SerializedName("tracks")
    LastfmTracks tracks;

    public LastfmTracksRoot() {
    }

    public LastfmTracks getTracks() {
        return tracks;
    }

    @Override
    public String toString() {
        return "LastfmTracksRoot{" +
                "tracks=" + tracks +
                '}';
    }
}

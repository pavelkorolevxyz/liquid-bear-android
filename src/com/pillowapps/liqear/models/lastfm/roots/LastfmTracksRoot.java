package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmTracks;

public class LastfmTracksRoot {
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

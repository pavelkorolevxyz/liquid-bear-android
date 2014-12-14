package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmTracks;

public class LastfmRecentTracksRoot {
    @SerializedName("recenttracks")
    LastfmTracks tracks;

    public LastfmRecentTracksRoot() {
    }

    public LastfmTracks getTracks() {
        return tracks;
    }

    @Override
    public String toString() {
        return "LastfmRecentTracksRoot{" +
                "tracks=" + tracks +
                '}';
    }
}

package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmTracksArtistStruct;

public class LastfmRecentTracksRoot extends LastfmResponse {
    @SerializedName("recenttracks")
    LastfmTracksArtistStruct tracks;

    public LastfmRecentTracksRoot() {
    }

    public LastfmTracksArtistStruct getTracks() {
        return tracks;
    }

    @Override
    public String toString() {
        return "LastfmRecentTracksRoot{" +
                "tracks=" + tracks +
                '}';
    }
}

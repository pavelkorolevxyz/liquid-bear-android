package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;

public class LastfmTrackRoot extends LastfmResponse {
    @SerializedName("track")
    private LastfmTrack track;

    public LastfmTrackRoot() {
    }

    public LastfmTrack getTrack() {
        return track;
    }
}

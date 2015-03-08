package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmTracksArtistStruct;

public class LastfmWeeklyTrackChartRoot {
    @SerializedName("weeklytrackchart")
    LastfmTracksArtistStruct tracks;

    public LastfmWeeklyTrackChartRoot() {
    }

    public LastfmTracksArtistStruct getTracks() {
        return tracks;
    }
}

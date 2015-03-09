package com.pillowapps.liqear.entities.setlistfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SetlistfmSet {
    @SerializedName("song")
    private List<SetlistfmTrack> tracks;

    public SetlistfmSet() {
    }

    public List<SetlistfmTrack> getTracks() {
        return tracks;
    }
}

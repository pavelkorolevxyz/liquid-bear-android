package com.pillowapps.liqear.entities.setlistfm;

import com.google.gson.annotations.SerializedName;

public class SetlistfmTrack {
    @SerializedName("@name")
    private String title;

    public SetlistfmTrack() {
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "SetlistfmTrack{" +
                "title='" + title + '\'' +
                '}';
    }
}

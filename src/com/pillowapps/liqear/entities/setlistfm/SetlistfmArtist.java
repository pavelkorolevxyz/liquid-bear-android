package com.pillowapps.liqear.entities.setlistfm;

import com.google.gson.annotations.SerializedName;

public class SetlistfmArtist {
    @SerializedName("@name")
    private String name;

    public SetlistfmArtist() {
    }

    public String getName() {
        return name;
    }
}

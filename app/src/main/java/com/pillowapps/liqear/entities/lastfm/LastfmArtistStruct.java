package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmArtistStruct {
    @SerializedName("#text")
    private String name;

    public LastfmArtistStruct() {
    }

    public String getName() {
        return name;
    }
}

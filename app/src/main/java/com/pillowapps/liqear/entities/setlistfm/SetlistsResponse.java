package com.pillowapps.liqear.entities.setlistfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SetlistsResponse {
    @SerializedName("setlist")
    private List<SetlistfmSetlist> setlists;

    public SetlistsResponse() {
    }

    public List<SetlistfmSetlist> getSetlists() {
        return setlists;
    }
}

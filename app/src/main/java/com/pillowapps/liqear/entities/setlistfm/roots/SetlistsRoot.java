package com.pillowapps.liqear.entities.setlistfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.setlistfm.SetlistsResponse;

public class SetlistsRoot {
    @SerializedName("setlists")
    private SetlistsResponse response;

    public SetlistsRoot() {
    }

    public SetlistsResponse getResponse() {
        return response;
    }
}

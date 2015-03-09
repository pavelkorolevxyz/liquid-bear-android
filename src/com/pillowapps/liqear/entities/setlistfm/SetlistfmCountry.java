package com.pillowapps.liqear.entities.setlistfm;

import com.google.gson.annotations.SerializedName;

public class SetlistfmCountry {
    @SerializedName("@name")
    private String name;

    public SetlistfmCountry() {
    }

    public String getName() {
        return name;
    }
}

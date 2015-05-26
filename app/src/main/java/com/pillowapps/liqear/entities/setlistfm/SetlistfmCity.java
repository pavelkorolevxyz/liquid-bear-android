package com.pillowapps.liqear.entities.setlistfm;

import com.google.gson.annotations.SerializedName;

public class SetlistfmCity {
    @SerializedName("country")
    private SetlistfmCountry country;
    @SerializedName("@name")
    private String name;

    public SetlistfmCity() {
    }

    public SetlistfmCountry getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }
}

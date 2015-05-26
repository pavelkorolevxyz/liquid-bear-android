package com.pillowapps.liqear.entities.setlistfm;

import com.google.gson.annotations.SerializedName;

public class SetlistfmVenue {
    @SerializedName("@name")
    private String name;
    @SerializedName("city")
    private SetlistfmCity city;

    public SetlistfmVenue() {
    }

    public String getName() {
        return name;
    }

    public SetlistfmCity getCity() {
        return city;
    }
}

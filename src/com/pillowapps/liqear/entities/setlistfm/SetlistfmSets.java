package com.pillowapps.liqear.entities.setlistfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SetlistfmSets {
    @SerializedName("set")
    private List<SetlistfmSet> sets;

    public SetlistfmSets() {
    }

    public List<SetlistfmSet> getSets() {
        return sets;
    }
}

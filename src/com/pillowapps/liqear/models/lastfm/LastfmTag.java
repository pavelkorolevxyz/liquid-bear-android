package com.pillowapps.liqear.models.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmTag {
    @SerializedName("name")
    String name;

    public LastfmTag() {
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "LastfmTags{" +
                "name='" + name + '\'' +
                '}';
    }
}

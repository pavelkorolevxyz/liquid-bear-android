package com.pillowapps.liqear.models.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmSession {
    @SerializedName("name")
    String name;
    @SerializedName("key")
    String key;

    public LastfmSession() {
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "LastfmSession{" +
                "name='" + name + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}

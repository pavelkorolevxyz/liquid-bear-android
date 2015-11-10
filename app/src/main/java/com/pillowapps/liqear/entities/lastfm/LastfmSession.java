package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmSession extends LastfmResponse {
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

package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmImage {
    @SerializedName("#text")
    String url;
    @SerializedName("size")
    String size;

    public LastfmImage() {
    }

    public String getUrl() {
        return url;
    }

    public String getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "LastfmImage{" +
                "url='" + url + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}

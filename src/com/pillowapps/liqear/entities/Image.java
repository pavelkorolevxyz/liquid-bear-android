package com.pillowapps.liqear.entities;

import com.google.gson.annotations.SerializedName;

public class Image {
    @SerializedName("#text")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

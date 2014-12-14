package com.pillowapps.liqear.models.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmTags {

    @SerializedName("tag")
    List<LastfmTag> tags;

    public LastfmTags() {
    }

    public List<LastfmTag> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "LastfmTags{" +
                "tags=" + tags +
                '}';
    }
}

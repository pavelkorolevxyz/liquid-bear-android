package com.pillowapps.liqear.models.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmTagSearchResults {
    @SerializedName("tagmatches")
    LastfmTags tags;

    public LastfmTagSearchResults() {
    }

    public LastfmTags getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "LastfmTagSearchResults{" +
                "tags=" + tags +
                '}';
    }
}

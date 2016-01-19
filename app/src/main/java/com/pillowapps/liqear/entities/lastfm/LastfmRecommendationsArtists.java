package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmRecommendationsArtists {
    @SerializedName("artist")
    private List<LastfmArtist> artists;

    public LastfmRecommendationsArtists() {
    }

    public List<LastfmArtist> getArtists() {
        return artists;
    }
}

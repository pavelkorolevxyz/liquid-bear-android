package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmRecommendationsArtists;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;

public class LastfmRecommendationsArtistRoot extends LastfmResponse {
    @SerializedName("recommendations")
    LastfmRecommendationsArtists recommendations;

    public LastfmRecommendationsArtistRoot() {
    }

    public LastfmRecommendationsArtists getRecommendations() {
        return recommendations;
    }
}

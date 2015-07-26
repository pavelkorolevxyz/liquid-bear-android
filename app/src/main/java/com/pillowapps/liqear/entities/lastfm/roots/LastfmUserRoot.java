package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmUser;

public class LastfmUserRoot extends LastfmResponse {
    @SerializedName("user")
    private LastfmUser user;

    public LastfmUserRoot() {
    }

    public LastfmUser getUser() {
        return user;
    }
}

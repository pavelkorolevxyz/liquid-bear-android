package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmUser;
import com.pillowapps.liqear.entities.vk.VkResponse;

public class LastfmUserRoot extends VkResponse {
    @SerializedName("user")
    private LastfmUser user;

    public LastfmUserRoot() {
    }

    public LastfmUser getUser() {
        return user;
    }
}

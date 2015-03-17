package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmUsers;

public class LastfmNeighboursRoot extends LastfmResponse {
    @SerializedName("neighbours")
    LastfmUsers users;

    public LastfmNeighboursRoot() {
    }

    public LastfmUsers getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "LastfmFriendsRoot{" +
                "users=" + users +
                '}';
    }
}

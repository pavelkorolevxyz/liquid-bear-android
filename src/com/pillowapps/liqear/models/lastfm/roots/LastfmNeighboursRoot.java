package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmUsers;

public class LastfmNeighboursRoot {
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

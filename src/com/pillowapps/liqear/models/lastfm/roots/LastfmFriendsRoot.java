package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmUsers;

public class LastfmFriendsRoot {
    @SerializedName("friends")
    LastfmUsers users;

    public LastfmFriendsRoot() {
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

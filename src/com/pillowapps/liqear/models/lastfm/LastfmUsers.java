package com.pillowapps.liqear.models.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmUsers {
    @SerializedName("user")
    List<LastfmUser> users;

    public LastfmUsers() {
    }

    public List<LastfmUser> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "LastfmUsers{" +
                "users=" + users +
                '}';
    }
}

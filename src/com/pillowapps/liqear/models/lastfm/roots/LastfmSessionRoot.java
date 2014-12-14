package com.pillowapps.liqear.models.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.models.lastfm.LastfmSession;

public class LastfmSessionRoot {
    @SerializedName("session")
    LastfmSession session;

    public LastfmSessionRoot() {
    }

    public LastfmSession getSession() {
        return session;
    }

    @Override
    public String toString() {
        return "LastfmSessionRoot{" +
                "session=" + session +
                '}';
    }
}

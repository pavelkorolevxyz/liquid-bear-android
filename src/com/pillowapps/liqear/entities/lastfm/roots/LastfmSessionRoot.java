package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmSession;

public class LastfmSessionRoot extends LastfmResponse{
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

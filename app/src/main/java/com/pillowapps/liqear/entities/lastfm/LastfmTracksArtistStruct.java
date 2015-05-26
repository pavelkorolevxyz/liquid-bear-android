package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmTracksArtistStruct {
    @SerializedName("track")
    List<LastfmTrackArtistStruct> tracks;

    public LastfmTracksArtistStruct() {
    }

    public List<LastfmTrackArtistStruct> getTracks() {
        return tracks;
    }

    @Override
    public String toString() {
        return "LastfmTracks{" +
                "tracks=" + tracks +
                '}';
    }
}

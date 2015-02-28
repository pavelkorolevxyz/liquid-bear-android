package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VkTracksResponse {
    @SerializedName("items")
    private List<VkTrack> tracks;
    @SerializedName("count")
    private int count;

    public VkTracksResponse() {
    }

    public List<VkTrack> getTracks() {
        return tracks;
    }

    public int getCount() {
        return count;
    }
}

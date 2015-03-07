package com.pillowapps.liqear.entities.vk.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.entities.vk.VkTracksResponse;

public class VkTracksResponseRoot extends VkResponse {
    @SerializedName("response")
    private VkTracksResponse response;
    @SerializedName("count")
    private int count;

    public VkTracksResponseRoot() {
    }

    public VkTracksResponse getResponse() {
        return response;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "VkTracksResponseRoot{" +
                "response=" + response +
                ", count=" + count +
                '}';
    }
}

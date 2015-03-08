package com.pillowapps.liqear.entities.vk.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.entities.vk.VkTrack;

import java.util.List;

public class VkTrackUrlResponseRoot extends VkResponse {
    @SerializedName("response")
    private List<VkTrack> response;

    public VkTrackUrlResponseRoot() {
    }

    public List<VkTrack> getResponse() {
        return response;
    }
}

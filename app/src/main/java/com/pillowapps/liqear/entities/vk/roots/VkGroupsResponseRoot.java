package com.pillowapps.liqear.entities.vk.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.vk.VkGroupsResponse;
import com.pillowapps.liqear.entities.vk.VkResponse;

public class VkGroupsResponseRoot extends VkResponse {
    @SerializedName("response")
    private VkGroupsResponse response;

    public VkGroupsResponseRoot() {
    }

    public VkGroupsResponse getResponse() {
        return response;
    }
}

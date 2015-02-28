package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

public class VkGroupsResponseRoot {
    @SerializedName("response")
    private VkGroupsResponse response;

    public VkGroupsResponseRoot() {
    }

    public VkGroupsResponse getResponse() {
        return response;
    }
}

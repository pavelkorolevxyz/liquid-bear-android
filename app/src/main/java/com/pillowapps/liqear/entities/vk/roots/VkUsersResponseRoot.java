package com.pillowapps.liqear.entities.vk.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.entities.vk.VkUsersResponse;

public class VkUsersResponseRoot extends VkResponse {
    @SerializedName("response")
    private VkUsersResponse response;

    public VkUsersResponseRoot() {
    }

    public VkUsersResponse getResponse() {
        return response;
    }
}

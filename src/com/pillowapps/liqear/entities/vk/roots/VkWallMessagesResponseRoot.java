package com.pillowapps.liqear.entities.vk.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.entities.vk.VkWallMessagesResponse;

public class VkWallMessagesResponseRoot extends VkResponse {
    @SerializedName("response")
    private VkWallMessagesResponse response;

    public VkWallMessagesResponseRoot() {
    }

    public VkWallMessagesResponse getResponse() {
        return response;
    }

}

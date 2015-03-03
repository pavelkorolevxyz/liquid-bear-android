package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

public class VkWallMessagesResponseRoot extends VkResponse {
    @SerializedName("response")
    private VkWallMessagesResponse response;

    public VkWallMessagesResponseRoot() {
    }

    public VkWallMessagesResponse getResponse() {
        return response;
    }

}

package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

public class VkAlbumsResponseRoot {
    @SerializedName("response")
    private VkAlbumsResponse response;

    public VkAlbumsResponse getResponse() {
        return response;
    }
}

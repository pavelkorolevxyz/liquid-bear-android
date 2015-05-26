package com.pillowapps.liqear.entities.vk.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.vk.VkAlbumsResponse;
import com.pillowapps.liqear.entities.vk.VkResponse;

public class VkAlbumsResponseRoot extends VkResponse {
    @SerializedName("response")
    private VkAlbumsResponse response;

    public VkAlbumsResponse getResponse() {
        return response;
    }
}

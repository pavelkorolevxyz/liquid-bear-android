package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VkUsersResponseRoot extends VkResponse {
    @SerializedName("response")
    private VkUsersResponse response;

    public VkUsersResponseRoot() {
    }

    public VkUsersResponse getResponse() {
        return response;
    }
}

package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VkUsersResponseRoot extends VkResponse {
    @SerializedName("response")
    private List<VkUser> users;

    public VkUsersResponseRoot() {
    }

    public List<VkUser> getUsers() {
        return users;
    }
}

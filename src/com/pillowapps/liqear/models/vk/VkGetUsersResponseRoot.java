package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VkGetUsersResponseRoot extends VkResponse {
    @SerializedName("response")
    private List<VkUser> users;

    public VkGetUsersResponseRoot() {
    }

    public List<VkUser> getUsers() {
        return users;
    }
}

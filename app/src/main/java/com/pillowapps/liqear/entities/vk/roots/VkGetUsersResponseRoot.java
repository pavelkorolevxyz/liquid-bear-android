package com.pillowapps.liqear.entities.vk.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.entities.vk.VkUser;

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

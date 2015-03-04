package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VkUsersResponse {
    @SerializedName("count")
    private int count;
    @SerializedName("items")
    List<VkUser> users;

    public VkUsersResponse() {
    }

    public int getCount() {
        return count;
    }

    public List<VkUser> getUsers() {
        return users;
    }
}

package com.pillowapps.liqear.entities.vk;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VkWallMessagesResponse {
    @SerializedName("items")
    private List<VkWallMessage> posts;
    @SerializedName("count")
    private int count;

    public VkWallMessagesResponse() {
    }

    public List<VkWallMessage> getPosts() {
        return posts;
    }

    public int getCount() {
        return count;
    }
}

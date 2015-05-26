package com.pillowapps.liqear.entities.vk;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VkGroupsResponse {
    @SerializedName("count")
    private int count;
    @SerializedName("items")
    private List<VkGroup> groups;

    public VkGroupsResponse() {
    }

    public int getCount() {
        return count;
    }

    public List<VkGroup> getGroups() {
        return groups;
    }
}

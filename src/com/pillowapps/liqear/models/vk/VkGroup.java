package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

public class VkGroup {
    @SerializedName("id")
    long id;
    @SerializedName("photo_100")
    String imageMedium;
    @SerializedName("name")
    String name;

    public VkGroup() {
    }

    public long getId() {
        return id;
    }

    public String getImageMedium() {
        return imageMedium;
    }

    public String getName() {
        return name;
    }
}

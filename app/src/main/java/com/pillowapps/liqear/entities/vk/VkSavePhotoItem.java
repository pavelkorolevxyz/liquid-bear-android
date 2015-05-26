package com.pillowapps.liqear.entities.vk;

import com.google.gson.annotations.SerializedName;

public class VkSavePhotoItem {
    @SerializedName("id")
    private long id;
    @SerializedName("owner_id")
    private long ownerId;

    public VkSavePhotoItem() {
    }

    public long getId() {
        return id;
    }

    public long getOwnerId() {
        return ownerId;
    }
}

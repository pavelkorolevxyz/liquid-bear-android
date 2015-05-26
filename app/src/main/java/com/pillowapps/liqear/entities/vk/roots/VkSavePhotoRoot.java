package com.pillowapps.liqear.entities.vk.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.vk.VkSavePhotoItem;

import java.util.List;

public class VkSavePhotoRoot {
    @SerializedName("response")
    private List<VkSavePhotoItem> photoItems;

    public VkSavePhotoRoot() {
    }

    public List<VkSavePhotoItem> getPhotoItems() {
        return photoItems;
    }
}

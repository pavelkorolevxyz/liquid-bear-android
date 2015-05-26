package com.pillowapps.liqear.entities.vk.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.vk.VkUploadServer;

public class VkUploadServerRoot {
    @SerializedName("response")
    private VkUploadServer uploadServer;

    public VkUploadServerRoot() {
    }

    public VkUploadServer getUploadServer() {
        return uploadServer;
    }
}

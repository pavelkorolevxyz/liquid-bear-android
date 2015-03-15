package com.pillowapps.liqear.entities.vk;

import com.google.gson.annotations.SerializedName;

public class VkPhotoUploadResult {
    @SerializedName("server")
    private String server;
    @SerializedName("photo")
    private String photo;
    @SerializedName("hash")
    private String hash;

    public VkPhotoUploadResult() {
    }

    public String getServer() {
        return server;
    }

    public String getPhoto() {
        return photo;
    }

    public String getHash() {
        return hash;
    }
}

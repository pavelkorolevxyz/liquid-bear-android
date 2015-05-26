package com.pillowapps.liqear.entities.vk;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

public class VkUploadServer {

    @SerializedName("upload_url")
    private URL uploadUrl;

    public VkUploadServer() {
    }

    public URL getUploadUrl() {
        return uploadUrl;
    }
}

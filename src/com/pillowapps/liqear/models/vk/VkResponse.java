package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

public class VkResponse {
    @SerializedName("error")
    VkError error;

    public VkResponse() {
    }

    public VkError getError() {
        return error;
    }
}

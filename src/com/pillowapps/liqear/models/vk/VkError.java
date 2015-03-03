package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

public class VkError {
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("error_msg")
    private String errorMessage;

    public VkError() {
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

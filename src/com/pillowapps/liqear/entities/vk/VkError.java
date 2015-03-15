package com.pillowapps.liqear.entities.vk;

import com.google.gson.annotations.SerializedName;

public class VkError {
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("error_msg")
    private String errorMessage;

    public VkError() {
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

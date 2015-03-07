package com.pillowapps.liqear.entities;

import com.google.gson.annotations.SerializedName;

public class LastfmResponse {
    @SerializedName("error")
    private int errorCode;
    @SerializedName("message")
    private String message;

    public LastfmResponse() {
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}

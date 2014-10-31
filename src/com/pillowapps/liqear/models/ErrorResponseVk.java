package com.pillowapps.liqear.models;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.R;

public class ErrorResponseVk {
    @SerializedName("method")
    private String method;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("error_msg")
    private String errorMessage;

    public ErrorResponseVk() {
    }

    public ErrorResponseVk(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ErrorResponseVk{" +
                "method='" + method + '\'' +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
    public String getTitle() {
        return String.format(LiqearApplication.getAppContext().getString(R.string.vk_error_title), errorCode, method);
    }
}

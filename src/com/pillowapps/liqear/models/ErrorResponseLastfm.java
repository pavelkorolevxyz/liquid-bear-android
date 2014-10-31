package com.pillowapps.liqear.models;

import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.R;

public class ErrorResponseLastfm {
    private int error;
    private String message;

    public ErrorResponseLastfm() {
    }

    public ErrorResponseLastfm(int error, String message) {
        this.error = error;
        this.message = message;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorResponseLastfm{" +
                "error=" + error +
                ", message='" + message + '\'' +
                '}';
    }

    public String getTitle() {
        return String.format(LiqearApplication.getAppContext().getString(R.string.lastfm_error_title), error);
    }
}

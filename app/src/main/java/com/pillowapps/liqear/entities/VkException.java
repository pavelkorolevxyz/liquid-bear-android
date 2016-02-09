package com.pillowapps.liqear.entities;

public class VkException extends RuntimeException {
    public VkException() {
    }

    public VkException(String detailMessage) {
        super(detailMessage);
    }

    public VkException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}

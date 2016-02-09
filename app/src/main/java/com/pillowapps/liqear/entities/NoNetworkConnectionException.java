package com.pillowapps.liqear.entities;

public class NoNetworkConnectionException extends RuntimeException {
    public NoNetworkConnectionException() {
    }

    public NoNetworkConnectionException(String detailMessage) {
        super(detailMessage);
    }

    public NoNetworkConnectionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}

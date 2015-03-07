package com.pillowapps.liqear.network;

public abstract class LastfmSimpleCallback<T> {
    public abstract void success(T data);

    public abstract void failure(String errorMessage);
}

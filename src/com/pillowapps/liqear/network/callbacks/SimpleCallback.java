package com.pillowapps.liqear.network.callbacks;

public abstract class SimpleCallback<T> {
    public abstract void success(T data);

    public abstract void failure(String errorMessage);
}

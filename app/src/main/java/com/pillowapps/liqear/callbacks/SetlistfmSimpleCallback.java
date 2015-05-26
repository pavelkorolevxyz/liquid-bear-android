package com.pillowapps.liqear.callbacks;

public abstract class SetlistfmSimpleCallback<T> {
    public abstract void success(T data);

    public abstract void failure(String errorMessage);
}

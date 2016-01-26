package com.pillowapps.liqear.callbacks.retrofit;

import com.pillowapps.liqear.entities.lastfm.LastfmResponse;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class LastfmErrorCallback<T extends LastfmResponse> implements Callback<T> {
    @Override
    public final void success(T data, Response response) {
        int errorCode = data.getErrorCode();
        if (errorCode == 0) {
            success(data);
        } else {
            failure(errorCode, data.getMessage());
        }
    }

    @Override
    public void failure(RetrofitError error) {
        failure(0, error.getMessage());
    }

    public abstract void success(T data);

    public abstract void failure(int code, String error);
}

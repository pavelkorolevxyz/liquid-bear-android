package com.pillowapps.liqear.callbacks;

import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkResponse;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class VkCallback<T extends VkResponse> implements Callback<T> {
    @Override
    public final void success(T data, Response response) {
        if (data.getError() == null) {
            success(data);
        } else {
            failure(data.getError());
        }
    }

    @Override
    public void failure(RetrofitError error) {
        failure(new VkError());
    }

    public abstract void success(T data);

    public abstract void failure(VkError error);
}

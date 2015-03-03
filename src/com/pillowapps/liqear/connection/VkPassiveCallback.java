package com.pillowapps.liqear.connection;

import com.pillowapps.liqear.models.vk.VkError;
import com.pillowapps.liqear.models.vk.VkResponse;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VkPassiveCallback extends VkSimpleCallback<VkResponse> {
    @Override
    public void success(VkResponse data) {

    }

    @Override
    public void failure(VkError error) {

    }
}

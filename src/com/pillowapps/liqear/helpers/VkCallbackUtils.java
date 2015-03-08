package com.pillowapps.liqear.helpers;

import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.network.callbacks.VkCallback;
import com.pillowapps.liqear.network.callbacks.VkSimpleCallback;

public class VkCallbackUtils {
    
    public static VkCallback<VkResponse> getTransitiveCallback(final VkSimpleCallback<VkResponse> callback) {
        return new VkCallback<VkResponse>() {
            @Override
            public void success(VkResponse data) {
                callback.success(data);
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        };
    }
}

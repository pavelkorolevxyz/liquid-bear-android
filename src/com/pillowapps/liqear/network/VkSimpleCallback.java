package com.pillowapps.liqear.network;

import com.pillowapps.liqear.models.vk.VkError;

public abstract class VkSimpleCallback<T> {
    public abstract void success(T data);

    public abstract void failure(VkError error);
}

package com.pillowapps.liqear.models.vk;

import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.callbacks.retrofit.VkCallback;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkGroup;
import com.pillowapps.liqear.entities.vk.roots.VkGroupsResponseRoot;
import com.pillowapps.liqear.network.service.VkApiService;

import java.util.List;

public class VkGroupModel {
    private VkApiService vkService;

    public VkGroupModel(VkApiService api) {
        this.vkService = api;
    }

    public void getGroups(int offset, int count, final VkSimpleCallback<List<VkGroup>> callback) {
        vkService.getGroups(1, offset, count, new VkCallback<VkGroupsResponseRoot>() {
            @Override
            public void success(VkGroupsResponseRoot data) {
                callback.success(data.getResponse().getGroups());
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }
}

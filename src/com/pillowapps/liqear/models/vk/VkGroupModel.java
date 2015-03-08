package com.pillowapps.liqear.models.vk;

import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkGroup;
import com.pillowapps.liqear.entities.vk.roots.VkGroupsResponseRoot;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.callbacks.VkCallback;
import com.pillowapps.liqear.network.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.network.service.VkApiService;

import java.util.List;

public class VkGroupModel {
    private VkApiService vkService = ServiceHelper.getVkService();

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

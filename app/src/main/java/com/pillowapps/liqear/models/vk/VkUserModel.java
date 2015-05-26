package com.pillowapps.liqear.models.vk;

import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkUser;
import com.pillowapps.liqear.entities.vk.roots.VkGetUsersResponseRoot;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.callbacks.VkCallback;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.network.service.VkApiService;

import java.util.List;

public class VkUserModel {
    private VkApiService vkService = ServiceHelper.getVkService();

    public void getUserInfoVk(long userId, final VkSimpleCallback<VkUser> callback) {
        String fields = "first_name,last_name,photo_medium";
        vkService.getUser(userId, fields, new VkCallback<VkGetUsersResponseRoot>() {
            @Override
            public void success(VkGetUsersResponseRoot data) {
                List<VkUser> users = data.getUsers();
                if (users == null || users.size() == 0) return;
                VkUser user = users.get(0);
                callback.success(user);
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }
}

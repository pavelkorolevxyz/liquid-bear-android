package com.pillowapps.liqear.connection;

import com.pillowapps.liqear.models.User;
import com.pillowapps.liqear.models.VkUser;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VkRequestManager {

    public static VkRequestManager instance;
    private VkApiService vkService = ServiceHelper.getVkService();

    private VkRequestManager() {

    }

    public static VkRequestManager getInstance() {
        if (instance == null) {
            instance = new VkRequestManager();
        }
        return instance;
    }

    public void getUsersInfoVk(long userId, Callback<VkUser> callback) {
//        vkService.getUsers(String.valueOf(userId), fields, new Callback<List<VkUser>>() {
//            @Override
//            public void success(List<User> users, Response response) {
//
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//
//            }
//        });
    }
}

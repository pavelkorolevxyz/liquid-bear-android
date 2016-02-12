package com.pillowapps.liqear.network;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.network.service.VkApiService;
import com.pillowapps.liqear.network.service.VkUploadService;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

@Module
public class VkApiModule {

    public static final String VK_API = "https://api.vk.com/method";

    public static final String VK_API_KEY = "03q8HwJ2xIgJlzxLgxv0";
    public static final String VK_APP_ID = "4613451";

    public static final String VK = "vk";

    @Provides
    @NonNull
    @Singleton
    public VkApiService provideVkApiService(@NonNull OkHttpClient okHttpClient, @NonNull @Named(VK) RequestInterceptor vkInterceptor) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(vkInterceptor)
                .setClient(new OkClient(okHttpClient))
                .setEndpoint(VK_API)
                .build();

        return restAdapter.create(VkApiService.class);
    }

    @Provides
    @NonNull
    public VkUploadService provideVkUploadService(@NonNull OkHttpClient okHttpClient, String uploadServer) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(uploadServer)
                .setClient(new OkClient(okHttpClient))
                .build();
        return restAdapter.create(VkUploadService.class);
    }

    @Provides
    @NonNull
    @Singleton
    @Named(VK)
    public RequestInterceptor provideVkInterceptor(AuthorizationInfoManager authorizationInfoManager) {
        return request -> {
            request.addQueryParam("access_token", authorizationInfoManager.getVkAccessToken());
            request.addQueryParam("v", "5.28");
        };
    }

}


package com.pillowapps.liqear.models;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.models.vk.VkAudioModel;
import com.pillowapps.liqear.models.vk.VkFriendModel;
import com.pillowapps.liqear.models.vk.VkGroupModel;
import com.pillowapps.liqear.models.vk.VkLyricsModel;
import com.pillowapps.liqear.models.vk.VkStatusModel;
import com.pillowapps.liqear.models.vk.VkUserModel;
import com.pillowapps.liqear.models.vk.VkWallModel;
import com.pillowapps.liqear.network.service.VkApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class VkModelsModule {

    @Provides
    @NonNull
    @Singleton
    public VkWallModel provideVkWallModel(@NonNull VkApiService api) {
        return new VkWallModel(api);
    }

    @Provides
    @NonNull
    @Singleton
    public VkAudioModel provideVkAudioModel(@NonNull VkApiService api) {
        return new VkAudioModel(api);
    }

    @Provides
    @NonNull
    @Singleton
    public VkFriendModel provideVkFriendModel(@NonNull VkApiService api) {
        return new VkFriendModel(api);
    }

    @Provides
    @NonNull
    @Singleton
    public VkGroupModel provideVkGroupModel(@NonNull VkApiService api) {
        return new VkGroupModel(api);
    }

    @Provides
    @NonNull
    @Singleton
    public VkLyricsModel provideVkLyricsModel(@NonNull VkApiService api) {
        return new VkLyricsModel(api);
    }

    @Provides
    @NonNull
    @Singleton
    public VkStatusModel provideVkStatusModel(@NonNull VkApiService api) {
        return new VkStatusModel(api);
    }

    @Provides
    @NonNull
    @Singleton
    public VkUserModel provideVkUserModel(@NonNull VkApiService api) {
        return new VkUserModel(api);
    }


}

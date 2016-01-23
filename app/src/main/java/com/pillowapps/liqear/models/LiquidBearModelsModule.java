package com.pillowapps.liqear.models;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.helpers.StorageManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LiquidBearModelsModule {

    @Provides
    @NonNull
    @Singleton
    public PlaylistModel providePlaylistsModel(StorageManager storageManager) {
        return new PlaylistModel(storageManager);
    }

}

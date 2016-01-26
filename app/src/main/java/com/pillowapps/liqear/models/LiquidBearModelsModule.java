package com.pillowapps.liqear.models;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.helpers.PlaylistsStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LiquidBearModelsModule {

    @Provides
    @NonNull
    @Singleton
    public PlaylistModel providePlaylistsModel(PlaylistsStorage storageManager) {
        return new PlaylistModel(storageManager);
    }

}

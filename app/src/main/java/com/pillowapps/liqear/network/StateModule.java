package com.pillowapps.liqear.network;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.helpers.StateManager;
import com.pillowapps.liqear.models.PlaylistModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StateModule {

    @Provides
    @NonNull
    @Singleton
    public StateManager provideStorageManager(PlaylistModel playlistModel, Timeline timeline) {
        return new StateManager(playlistModel, timeline);
    }

}


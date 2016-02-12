package com.pillowapps.liqear.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.helpers.LBPreferencesManager;
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
    public StateManager provideStorageManager(Context context, PlaylistModel playlistModel, Timeline timeline) {
        return new StateManager(context, playlistModel, timeline);
    }

    @Provides
    @NonNull
    @Singleton
    public LBPreferencesManager providePreferencesManager(Context context) {
        return new LBPreferencesManager(context);
    }

}


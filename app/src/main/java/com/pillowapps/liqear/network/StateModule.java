package com.pillowapps.liqear.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.helpers.PreferencesScreenManager;
import com.pillowapps.liqear.helpers.SavesManager;
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
    public StateManager provideStorageManager(Context context, SavesManager savesManager, PlaylistModel playlistModel, Timeline timeline) {
        return new StateManager(context, savesManager, playlistModel, timeline);
    }

    @Provides
    @NonNull
    @Singleton
    public PreferencesScreenManager providePreferencesManager(Context context) {
        return new PreferencesScreenManager(context);
    }

    @Provides
    @NonNull
    @Singleton
    public SavesManager provideSavesManager(Context context) {
        return new SavesManager(context);
    }

}


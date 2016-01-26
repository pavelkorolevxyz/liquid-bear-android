package com.pillowapps.liqear.audio;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.helpers.MusicServiceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MusicServiceModule {

    @Provides
    @NonNull
    @Singleton
    public MusicServiceManager provideMusicServiceManager() {
        return new MusicServiceManager();
    }

}

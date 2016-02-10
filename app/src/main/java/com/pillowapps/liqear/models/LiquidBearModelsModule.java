package com.pillowapps.liqear.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pillowapps.liqear.audio.Timeline;
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

    @Provides
    @NonNull
    @Singleton
    public ImageModel provideImageModel() {
        return new ImageModel();
    }

    @Provides
    @NonNull
    @Singleton
    public ShareModel provideShareModel() {
        return new ShareModel();
    }

    @Provides
    @NonNull
    @Singleton
    public TutorialModel provideTutorialModel() {
        return new TutorialModel();
    }

    @Provides
    @NonNull
    @Singleton
    public TrackNotificationModel provideTrackNotificationModel(Context context, Timeline timeline) {
        return new TrackNotificationModel(context, timeline);
    }


}

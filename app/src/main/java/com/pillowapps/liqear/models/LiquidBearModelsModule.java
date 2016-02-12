package com.pillowapps.liqear.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.helpers.NetworkModel;
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
    public TutorialModel provideTutorialModel(Context context) {
        return new TutorialModel(context);
    }

    @Provides
    @NonNull
    @Singleton
    public TrackNotificationModel provideTrackNotificationModel(Context context, Timeline timeline) {
        return new TrackNotificationModel(context, timeline);
    }

    @Provides
    @NonNull
    @Singleton
    public AuthorizationInfoManager provideAuthorizationInfoManager(Context context) {
        return new AuthorizationInfoManager(context);
    }

    @Provides
    @NonNull
    @Singleton
    public ModeItemsHelper provideModeItemsHelper(Context context, AuthorizationInfoManager authorizationInfoManager, NetworkModel networkModel) {
        return new ModeItemsHelper(context, authorizationInfoManager, networkModel);
    }


}

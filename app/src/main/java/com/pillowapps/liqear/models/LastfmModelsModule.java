package com.pillowapps.liqear.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pillowapps.liqear.models.lastfm.LastfmAlbumModel;
import com.pillowapps.liqear.models.lastfm.LastfmArtistModel;
import com.pillowapps.liqear.models.lastfm.LastfmAuthModel;
import com.pillowapps.liqear.models.lastfm.LastfmChartModel;
import com.pillowapps.liqear.models.lastfm.LastfmLibraryModel;
import com.pillowapps.liqear.models.lastfm.LastfmRecommendationsModel;
import com.pillowapps.liqear.models.lastfm.LastfmTagModel;
import com.pillowapps.liqear.models.lastfm.LastfmTrackModel;
import com.pillowapps.liqear.models.lastfm.LastfmUserModel;
import com.pillowapps.liqear.network.service.LastfmApiService;
import com.pillowapps.liqear.network.service.LastfmAuthService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LastfmModelsModule {

    @Provides
    @NonNull
    @Singleton
    public LastfmAuthModel provideLastfmAuthModel(@NonNull LastfmAuthService api) {
        return new LastfmAuthModel(api);
    }

    @Provides
    @NonNull
    @Singleton
    public LastfmAlbumModel provideLastfmAlbumModel(@NonNull Context context,
                                                    @NonNull LastfmApiService api,
                                                    @NonNull ImageModel imageModel) {
        return new LastfmAlbumModel(context, api, imageModel);
    }

    @Provides
    @NonNull
    @Singleton
    public LastfmArtistModel provideLastfmArtistModel(@NonNull LastfmApiService api) {
        return new LastfmArtistModel(api);
    }

    @Provides
    @NonNull
    @Singleton
    public LastfmChartModel provideLastfmChartModel(@NonNull LastfmApiService api) {
        return new LastfmChartModel(api);
    }

    @Provides
    @NonNull
    @Singleton
    public LastfmTagModel provideLastfmTagModel(@NonNull LastfmApiService api) {
        return new LastfmTagModel(api);
    }

    @Provides
    @NonNull
    @Singleton
    public LastfmUserModel provideLastfmUserModel(@NonNull LastfmApiService api) {
        return new LastfmUserModel(api);
    }

    @Provides
    @NonNull
    @Singleton
    public LastfmTrackModel provideLastfmTrackModel(@NonNull LastfmApiService api) {
        return new LastfmTrackModel(api);
    }

    @Provides
    @NonNull
    @Singleton
    public LastfmRecommendationsModel provideLastfmRecommendationsModel(@NonNull LastfmArtistModel lastfmArtistModel) {
        return new LastfmRecommendationsModel(lastfmArtistModel);
    }

    @Provides
    @NonNull
    @Singleton
    public LastfmLibraryModel provideLastfmLibraryModel(@NonNull LastfmUserModel lastfmUserModel) {
        return new LastfmLibraryModel(lastfmUserModel);
    }

}

package com.pillowapps.liqear.models;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.models.setlistsfm.SetlistsfmSetlistModel;
import com.pillowapps.liqear.network.service.SetlistfmApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SetlistfmModelsModule {

    @Provides
    @NonNull
    @Singleton
    public SetlistsfmSetlistModel provideSetlistsfmSetlistModel(@NonNull SetlistfmApiService api) {
        return new SetlistsfmSetlistModel(api);
    }

}

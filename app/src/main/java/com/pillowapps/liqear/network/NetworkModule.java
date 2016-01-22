package com.pillowapps.liqear.network;

import android.support.annotation.NonNull;

import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkModule {

    @Provides
    @NonNull
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }

}


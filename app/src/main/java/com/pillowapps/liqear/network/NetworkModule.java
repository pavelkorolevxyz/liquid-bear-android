package com.pillowapps.liqear.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pillowapps.liqear.helpers.NetworkManager;
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

    @Provides
    @NonNull
    @Singleton
    public NetworkManager provideNetworkModel(@NonNull Context context) {
        return new NetworkManager(context);
    }

}


package com.pillowapps.liqear.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pillowapps.liqear.helpers.StorageManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {

    @Provides
    @NonNull
    @Singleton
    public StorageManager provideStorageManager(Context context) {
        return new StorageManager(context);
    }

}


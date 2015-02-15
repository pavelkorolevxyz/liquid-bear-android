package com.pillowapps.liqear;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pillowapps.liqear.global.Config;
import com.pillowapps.liqear.helpers.BuildModeHelper;

import timber.log.Timber;

public class LiqearApplication extends Application {
    private static Context context;

    public static Context getAppContext() {
        return LiqearApplication.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LiqearApplication.context = getApplicationContext();
        if (!BuildModeHelper.DEBUG) {
            Crashlytics.start(this);
        }

        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration.Builder(getApplicationContext())
                        .memoryCache(new LRULimitedMemoryCache(16 * 1024 * 1024))
                        .build();
        ImageLoader.getInstance().init(config);
        Timber.plant(new Timber.DebugTree());
        initConfig();
    }

    private void initConfig() {
        Config.resources = getResources();
    }
}
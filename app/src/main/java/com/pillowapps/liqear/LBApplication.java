package com.pillowapps.liqear;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class LBApplication extends Application {
    private static Context context;
    public static final Bus bus = new Bus(ThreadEnforcer.ANY);

    public static Context getAppContext() {
        return LBApplication.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LBApplication.context = getApplicationContext();

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        } else {
            Timber.plant(new Timber.DebugTree());
        }

//        ImageLoaderConfiguration config =
//                new ImageLoaderConfiguration.Builder(getApplicationContext())
//                        .memoryCache(new LRULimitedMemoryCache(16 * 1024 * 1024))
//                        .build();
//        ImageLoader.getInstance().init(config);

        ButterKnife.setDebug(true);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/PTC55F.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        FlowManager.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
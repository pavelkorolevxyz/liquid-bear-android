package com.pillowapps.liqear;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.pillowapps.liqear.audio.MusicServiceModule;
import com.pillowapps.liqear.models.LastfmModelsModule;
import com.pillowapps.liqear.models.LiquidBearModelsModule;
import com.pillowapps.liqear.models.SetlistfmModelsModule;
import com.pillowapps.liqear.models.VkModelsModule;
import com.pillowapps.liqear.network.LastfmApiModule;
import com.pillowapps.liqear.network.NetworkModule;
import com.pillowapps.liqear.network.SetlistfmApiModule;
import com.pillowapps.liqear.network.StateModule;
import com.pillowapps.liqear.network.StorageModule;
import com.pillowapps.liqear.network.VkApiModule;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class LBApplication extends Application {
    private static Context context;
    public static final Bus BUS = new Bus(ThreadEnforcer.ANY);

    @SuppressWarnings("NullableProblems")
    @NonNull
    private ApplicationComponent applicationComponent;

    // todo remove this method
    public static Context getAppContext() {
        return LBApplication.context;
    }

    @NonNull
    public static LBApplication get(@NonNull Context context) {
        return (LBApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = prepareApplicationComponent().build();
        applicationComponent.inject(this);

        LBApplication.context = getApplicationContext();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Fabric.with(this, new Crashlytics());
        }

        ButterKnife.setDebug(true);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/PTC55F.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    @NonNull
    protected DaggerApplicationComponent.Builder prepareApplicationComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule())
                .stateModule(new StateModule())
                .storageModule(new StorageModule())
                .musicServiceModule(new MusicServiceModule())
                .liquidBearModelsModule(new LiquidBearModelsModule())
                .lastfmApiModule(new LastfmApiModule())
                .vkApiModule(new VkApiModule())
                .setlistfmApiModule(new SetlistfmApiModule())
                .lastfmModelsModule(new LastfmModelsModule())
                .vkModelsModule(new VkModelsModule())
                .setlistfmModelsModule(new SetlistfmModelsModule());
    }

    @NonNull
    public ApplicationComponent applicationComponent() {
        return applicationComponent;
    }

}
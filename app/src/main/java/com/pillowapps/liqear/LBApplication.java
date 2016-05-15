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

public class LBApplication extends Application {
    public static final Bus BUS = new Bus(ThreadEnforcer.ANY);

    @SuppressWarnings("NullableProblems")
    @NonNull
    private ApplicationComponent applicationComponent;

    @NonNull
    public static LBApplication get(@NonNull Context context) {
        return (LBApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = prepareApplicationComponent().build();
        applicationComponent.inject(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            ButterKnife.setDebug(true);
        } else {
            Fabric.with(this, new Crashlytics());
        }
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
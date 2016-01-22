package com.pillowapps.liqear;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    @NonNull
    private final LBApplication application;

    public ApplicationModule(@NonNull LBApplication application) {
        this.application = application;
    }

    @Provides
    @NonNull
    @Singleton
    public LBApplication provideApplication() {
        return application;
    }

}

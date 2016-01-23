package com.pillowapps.liqear.network;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pillowapps.liqear.adapters.gson.LastfmAlbumMatchesGsonAdapter;
import com.pillowapps.liqear.adapters.gson.LastfmBooleanGsonAdapter;
import com.pillowapps.liqear.adapters.gson.LastfmTrackListGsonAdapter;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbums;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.network.service.LastfmApiService;
import com.pillowapps.liqear.network.service.LastfmAuthService;
import com.squareup.okhttp.OkHttpClient;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module
public class LastfmApiModule {

    public static final String LASTFM_API_HTTPS = "https://ws.audioscrobbler.com/2.0";
    public static final String LASTFM_API = "http://ws.audioscrobbler.com/2.0";

    public static final String LASTFM_API_KEY = "d5de674bc94e88b751606051c2570f48";
    public static final String LASTFM_API_SECRET = "2b1780635fa1baa06af78bd9e90ff7e3";

    public static final String LASTFM = "lastfm";

    @Provides
    @NonNull
    @Singleton
    public LastfmApiService provideLastfmApiService(@NonNull OkHttpClient okHttpClient,
                                                    @NonNull @Named(LASTFM) RequestInterceptor lastfmInterceptor) {
        LastfmApiService lastfmApiService;
        Type trackListTypeAdapter = new TypeToken<List<LastfmTrack>>() {
        }.getType();
        Type booleanTypeAdapter = new TypeToken<Boolean>() {
        }.getType();
        Type albumMatchesAdapter = new TypeToken<LastfmAlbums>() {
        }.getType();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(trackListTypeAdapter, new LastfmTrackListGsonAdapter())
                .registerTypeAdapter(booleanTypeAdapter, new LastfmBooleanGsonAdapter())
                .registerTypeAdapter(albumMatchesAdapter, new LastfmAlbumMatchesGsonAdapter())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .setClient(new OkClient(okHttpClient))
                .setRequestInterceptor(lastfmInterceptor)
                .setEndpoint(LASTFM_API)
                .build();

        lastfmApiService = restAdapter.create(LastfmApiService.class);

        return lastfmApiService;
    }

    @Provides
    @NonNull
    @Singleton
    public LastfmAuthService provideLastfmAuthService(@NonNull OkHttpClient okHttpClient,
                                                      @NonNull @Named(LASTFM) RequestInterceptor lastfmInterceptor) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(lastfmInterceptor)
                .setClient(new OkClient(okHttpClient))
                .setEndpoint(LASTFM_API_HTTPS)
                .build();
        return restAdapter.create(LastfmAuthService.class);
    }

    @Provides
    @Named(LASTFM)
    @NonNull
    @Singleton
    public RequestInterceptor provideLastmInterceptor() {
        return request -> {
            request.addQueryParam("api_key", LASTFM_API_KEY);
            request.addQueryParam("format", "json");
        };
    }

}


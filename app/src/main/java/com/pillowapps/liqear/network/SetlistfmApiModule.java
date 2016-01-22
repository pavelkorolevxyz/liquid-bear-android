package com.pillowapps.liqear.network;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pillowapps.liqear.adapters.gson.SetlistfmSetListGsonAdapter;
import com.pillowapps.liqear.adapters.gson.SetlistfmSetsAdapter;
import com.pillowapps.liqear.adapters.gson.SetlistfmSongListGsonAdapter;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSet;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSets;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmTrack;
import com.pillowapps.liqear.network.service.SetlistfmApiService;
import com.squareup.okhttp.OkHttpClient;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module
public class SetlistfmApiModule {

    public static final String SETLISTFM_API = "http://api.setlist.fm/rest/0.1";

    @Provides
    @NonNull
    @Singleton
    public SetlistfmApiService provideSetlistfmApiService(@NonNull OkHttpClient okHttpClient) {
        Type setsTypeAdapter = new TypeToken<SetlistfmSets>() {
        }.getType();
        Type setListTypeAdapter = new TypeToken<List<SetlistfmSet>>() {
        }.getType();
        Type trackListTypeAdapter = new TypeToken<List<SetlistfmTrack>>() {
        }.getType();

        Gson gson = new GsonBuilder()
                .setDateFormat("dd-MM-yyyy")
                .registerTypeAdapter(setListTypeAdapter, new SetlistfmSetListGsonAdapter())
                .registerTypeAdapter(trackListTypeAdapter, new SetlistfmSongListGsonAdapter())
                .registerTypeAdapter(setsTypeAdapter, new SetlistfmSetsAdapter())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(okHttpClient))
                .setConverter(new GsonConverter(gson))
                .setEndpoint(SETLISTFM_API)
                .build();

        return restAdapter.create(SetlistfmApiService.class);
    }

}


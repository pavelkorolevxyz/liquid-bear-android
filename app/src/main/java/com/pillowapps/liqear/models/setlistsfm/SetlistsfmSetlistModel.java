package com.pillowapps.liqear.models.setlistsfm;

import com.pillowapps.liqear.callbacks.SetlistfmSimpleCallback;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSetlist;
import com.pillowapps.liqear.entities.setlistfm.roots.SetlistsRoot;
import com.pillowapps.liqear.network.service.SetlistfmApiService;

import java.util.List;
import java.util.TreeMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class SetlistsfmSetlistModel {
    private SetlistfmApiService setlistfmService;

    public SetlistsfmSetlistModel(SetlistfmApiService api) {
        this.setlistfmService = api;
    }

    public void getSetlists(String artist, String city, String venue,
                            final SetlistfmSimpleCallback<List<SetlistfmSetlist>> callback) {
        TreeMap<String, String> params = new TreeMap<>();
        Timber.d(artist + " " + city + " " + venue);
        if (artist != null && !artist.isEmpty()) params.put("artistName", artist);
        if (city != null && !city.isEmpty()) params.put("cityName", city);
        if (venue != null && !venue.isEmpty()) params.put("venueName", venue);
        setlistfmService.getSetlists(params, new Callback<SetlistsRoot>() {
            @Override
            public void success(SetlistsRoot setlistsRoot, Response response) {
                callback.success(setlistsRoot.getResponse().getSetlists());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                callback.failure(error.getMessage());
            }
        });
    }
}

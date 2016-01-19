package com.pillowapps.liqear.network.service;

import com.pillowapps.liqear.entities.setlistfm.roots.SetlistsRoot;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;

public interface SetlistfmService {

    @GET("/search/setlists.json")
    void getSetlists(@QueryMap Map<String, String> params,
                     Callback<SetlistsRoot> callback);

}

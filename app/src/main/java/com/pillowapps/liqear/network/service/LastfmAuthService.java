package com.pillowapps.liqear.network.service;

import com.pillowapps.liqear.entities.lastfm.roots.LastfmSessionRoot;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface LastfmAuthService {
    @FormUrlEncoded()
    @POST("/?method=auth.getMobileSession")
    void getMobileSession(@Field("username") String user,
                          @Field("password") String password,
                          @Field("api_sig") String apiSig,
                          Callback<LastfmSessionRoot> callback);

}

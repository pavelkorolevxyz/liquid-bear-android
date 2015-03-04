package com.pillowapps.liqear.network;

import com.pillowapps.liqear.models.lastfm.roots.LastfmSessionRoot;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface LastfmAuthService {
    @FormUrlEncoded()
    @POST("/?method=auth.getMobileSession")
    public void getMobileSession(@Field("username") String user,
                                 @Field("password") String password,
                                 @Field("api_sig") String apiSig,
                                 Callback<LastfmSessionRoot> callback);

}

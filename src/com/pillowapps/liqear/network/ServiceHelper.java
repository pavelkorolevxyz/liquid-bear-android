package com.pillowapps.liqear.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.LastfmTrackListGsonAdapter;
import com.pillowapps.liqear.models.lastfm.LastfmTrack;

import java.lang.reflect.Type;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class ServiceHelper {

    public static final String VK_API = "https://api.vk.com/method";
    public static final String LASTFM_API_HTTPS = "https://ws.audioscrobbler.com/2.0";
    public static final String LASTFM_API = "http://ws.audioscrobbler.com/2.0";

    public static final String LASTFM_API_KEY = "d5de674bc94e88b751606051c2570f48";
    public static final String LASTFM_API_SECRET = "2b1780635fa1baa06af78bd9e90ff7e3";

    public static final String VK_API_KEY = "03q8HwJ2xIgJlzxLgxv0";
    public static final String VK_APP_ID = "4613451";

    private static VkApiService vkApiService;
    private static LastfmApiService lastfmApiService;

    private static RequestInterceptor lastfmInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestInterceptor.RequestFacade request) {
            request.addQueryParam("api_key", LASTFM_API_KEY);
            request.addQueryParam("format", "json");
        }
    };

    private ServiceHelper() {
        // No op.
    }

    public static VkApiService getVkService() {
        if (vkApiService == null) {
            RequestInterceptor requestInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addQueryParam("access_token", AuthorizationInfoManager.getVkAccessToken());
                    request.addQueryParam("v", "5.28");
                }
            };

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setRequestInterceptor(requestInterceptor)
                    .setEndpoint(VK_API)
                    .build();

            //access_token
            vkApiService = restAdapter.create(VkApiService.class);
        }
        return vkApiService;
    }

    public static LastfmApiService getLastfmService() {
        if (lastfmApiService == null) {
            Type myOtherClassListType = new TypeToken<List<LastfmTrack>>() {
            }.getType();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(myOtherClassListType, new LastfmTrackListGsonAdapter())
                    .create();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setConverter(new GsonConverter(gson))
                    .setRequestInterceptor(lastfmInterceptor)
                    .setEndpoint(LASTFM_API)
                    .build();
            lastfmApiService = restAdapter.create(LastfmApiService.class);
        }

        return lastfmApiService;
    }

    public static LastfmAuthService getLastfmAuthService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(lastfmInterceptor)
                .setEndpoint(LASTFM_API_HTTPS)
                .build();
        return restAdapter.create(LastfmAuthService.class);

    }

    public static VkUploadService getVkUploadService(String uploadServer) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(uploadServer)
                .build();
        return restAdapter.create(VkUploadService.class);
    }
}

package com.pillowapps.liqear.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pillowapps.liqear.adapters.gson.LastfmBooleanGsonAdapter;
import com.pillowapps.liqear.adapters.gson.LastfmTrackListGsonAdapter;
import com.pillowapps.liqear.adapters.gson.SetlistfmSetListGsonAdapter;
import com.pillowapps.liqear.adapters.gson.SetlistfmSetsAdapter;
import com.pillowapps.liqear.adapters.gson.SetlistfmSongListGsonAdapter;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSet;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSets;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmTrack;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.network.service.LastfmApiService;
import com.pillowapps.liqear.network.service.LastfmAuthService;
import com.pillowapps.liqear.network.service.SetlistfmService;
import com.pillowapps.liqear.network.service.VkApiService;
import com.pillowapps.liqear.network.service.VkUploadService;
import com.squareup.okhttp.OkHttpClient;

import java.lang.reflect.Type;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class ServiceHelper {

    public static final String VK_API = "https://api.vk.com/method";
    public static final String LASTFM_API_HTTPS = "https://ws.audioscrobbler.com/2.0";
    public static final String LASTFM_API = "http://ws.audioscrobbler.com/2.0";
    public static final String SETLISTFM_API = "http://api.setlist.fm/rest/0.1";

    public static final String LASTFM_API_KEY = "d5de674bc94e88b751606051c2570f48";
    public static final String LASTFM_API_SECRET = "2b1780635fa1baa06af78bd9e90ff7e3";
    public static final String VK_API_KEY = "03q8HwJ2xIgJlzxLgxv0";
    public static final String VK_APP_ID = "4613451";

    private static VkApiService vkApiService;
    private static LastfmApiService lastfmApiService;
    private static SetlistfmService setlistfmService;

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
            RequestInterceptor vkRequestInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addQueryParam("access_token", AuthorizationInfoManager.getVkAccessToken());
                    request.addQueryParam("v", "5.28");
                }
            };
            OkHttpClient okHttpClient = new OkHttpClient();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setRequestInterceptor(vkRequestInterceptor)
                    .setClient(new OkClient(okHttpClient))
                    .setEndpoint(VK_API)
                    .build();

            vkApiService = restAdapter.create(VkApiService.class);
        }
        return vkApiService;
    }

    public static LastfmApiService getLastfmService() {
        if (lastfmApiService == null) {
            Type trackListTypeAdapter = new TypeToken<List<LastfmTrack>>() {
            }.getType();
            Type booleanTypeAdapter = new TypeToken<Boolean>() {
            }.getType();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(trackListTypeAdapter, new LastfmTrackListGsonAdapter())
                    .registerTypeAdapter(booleanTypeAdapter, new LastfmBooleanGsonAdapter())
                    .create();

            OkHttpClient okHttpClient = new OkHttpClient();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setConverter(new GsonConverter(gson))
                    .setClient(new OkClient(okHttpClient))
                    .setRequestInterceptor(lastfmInterceptor)
                    .setEndpoint(LASTFM_API)
                    .build();

            lastfmApiService = restAdapter.create(LastfmApiService.class);
        }

        return lastfmApiService;
    }

    public static LastfmAuthService getLastfmAuthService() {
        OkHttpClient okHttpClient = new OkHttpClient();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(lastfmInterceptor)
                .setClient(new OkClient(okHttpClient))
                .setEndpoint(LASTFM_API_HTTPS)
                .build();
        return restAdapter.create(LastfmAuthService.class);

    }

    public static VkUploadService getVkUploadService(String uploadServer) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(uploadServer)
                .setClient(new OkClient(okHttpClient))
                .build();
        return restAdapter.create(VkUploadService.class);
    }

    public static SetlistfmService getSetlistsfmService() {
        if (setlistfmService == null) {
            Gson gson = getSetlistfmGson();

            OkHttpClient okHttpClient = new OkHttpClient();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setClient(new OkClient(okHttpClient))
                    .setConverter(new GsonConverter(gson))
                    .setEndpoint(SETLISTFM_API)
                    .build();

            setlistfmService = restAdapter.create(SetlistfmService.class);
        }
        return setlistfmService;
    }

    private static Gson getSetlistfmGson() {
        Type setsTypeAdapter = new TypeToken<SetlistfmSets>() {
        }.getType();
        Type setListTypeAdapter = new TypeToken<List<SetlistfmSet>>() {
        }.getType();
        Type trackListTypeAdapter = new TypeToken<List<SetlistfmTrack>>() {
        }.getType();
        return new GsonBuilder()
                .setDateFormat("dd-MM-yyyy")
                .registerTypeAdapter(setListTypeAdapter, new SetlistfmSetListGsonAdapter())
                .registerTypeAdapter(trackListTypeAdapter, new SetlistfmSongListGsonAdapter())
                .registerTypeAdapter(setsTypeAdapter, new SetlistfmSetsAdapter())
                .create();
    }
}

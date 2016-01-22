package com.pillowapps.liqear.models.lastfm;

import com.pillowapps.liqear.callbacks.LastfmErrorCodeCallback;
import com.pillowapps.liqear.entities.lastfm.LastfmSession;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmSessionRoot;
import com.pillowapps.liqear.helpers.LastfmApiHelper;
import com.pillowapps.liqear.network.service.LastfmAuthService;

import java.util.TreeMap;

public class LastfmAuthModel {
    private LastfmAuthService authService;
    private LastfmApiHelper apiHelper = new LastfmApiHelper();

    public LastfmAuthModel(LastfmAuthService api) {
        this.authService = api;
    }

    public void getMobileSession(String username, String password, final LastfmErrorCodeCallback<LastfmSession> callback) {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("method", "auth.getMobileSession");
        authService.getMobileSession(
                username,
                password,
                apiHelper.generateApiSig(params),
                new LastfmErrorCodeCallback<LastfmSessionRoot>() {
                    @Override
                    public void success(LastfmSessionRoot data) {
                        callback.success(data.getSession());
                    }

                    @Override
                    public void failure(int code, String error) {
                        callback.failure(code, error);
                    }
                }
        );
    }
}

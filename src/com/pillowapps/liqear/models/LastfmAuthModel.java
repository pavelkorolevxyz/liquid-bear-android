package com.pillowapps.liqear.models;

import com.pillowapps.liqear.entities.lastfm.LastfmSession;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmSessionRoot;
import com.pillowapps.liqear.helpers.LastfmApiHelper;
import com.pillowapps.liqear.network.LastfmCallback;
import com.pillowapps.liqear.network.LastfmSimpleCallback;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.service.LastfmAuthService;

import java.util.TreeMap;

public class LastfmAuthModel {
    private LastfmAuthService authService = ServiceHelper.getLastfmAuthService();
    private LastfmApiHelper apiHelper = new LastfmApiHelper();

    public void getMobileSession(String username, String password, final LastfmSimpleCallback<LastfmSession> callback) {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("method", "auth.getMobileSession");
        authService.getMobileSession(
                username,
                password,
                apiHelper.generateApiSig(params),
                new LastfmCallback<LastfmSessionRoot>() {
                    @Override
                    public void success(LastfmSessionRoot data) {
                        callback.success(data.getSession());
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                }
        );
    }
}

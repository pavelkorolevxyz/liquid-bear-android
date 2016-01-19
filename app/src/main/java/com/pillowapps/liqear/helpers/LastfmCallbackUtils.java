package com.pillowapps.liqear.helpers;

import com.pillowapps.liqear.entities.lastfm.LastfmResponse;
import com.pillowapps.liqear.callbacks.LastfmCallback;
import com.pillowapps.liqear.callbacks.SimpleCallback;

public class LastfmCallbackUtils {

    private LastfmCallbackUtils() {
        // no-op
    }

    public static LastfmCallback<LastfmResponse> createTransitiveCallback(final SimpleCallback<Object> callback) {
        return new LastfmCallback<LastfmResponse>() {
            @Override
            public void success(LastfmResponse data) {
                callback.success(data);
            }

            @Override
            public void failure(String error) {
                callback.failure(error);
            }
        };
    }

}

package com.pillowapps.liqear.connection;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PassiveCallback implements Callback<Object> {
    @Override
    public void success(Object t, Response response) {
        // No operation.
    }

    @Override
    public void failure(RetrofitError error) {
        // No operation.
    }
}

package com.pillowapps.liqear.helpers;

import android.content.Context;
import android.net.ConnectivityManager;

import com.pillowapps.liqear.LBApplication;

public class NetworkModel {

    private Context context;

    public NetworkModel(Context context) {
        this.context = context;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm != null && cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}

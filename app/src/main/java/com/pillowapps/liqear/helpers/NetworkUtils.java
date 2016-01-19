package com.pillowapps.liqear.helpers;

import android.content.Context;
import android.net.ConnectivityManager;

import com.pillowapps.liqear.LBApplication;

public class NetworkUtils {

    private NetworkUtils() {
        // no-op
    }

    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) LBApplication.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm != null && cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}

package com.pillowapps.liqear.helpers;

import android.util.Log;

public class LLog {
    public static final String LOG_TAG = "Liquid_Bear";

    public static void log(String s) {
        if (!BuildModeHelper.DEBUG) return;
        Log.e(LOG_TAG, s);
    }

    public static void log(Object s) {
        if (!BuildModeHelper.DEBUG) return;
        Log.e(LOG_TAG, String.valueOf(s));
    }

    public static void logd(String s) {
        if (!BuildModeHelper.DEBUG) return;
        Log.d(LOG_TAG, s);
    }

    public static void d(Object object) {
        if (!BuildModeHelper.DEBUG) return;
        Log.d(LOG_TAG, String.valueOf(object));
    }
}

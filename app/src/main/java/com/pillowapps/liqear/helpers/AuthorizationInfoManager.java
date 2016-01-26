package com.pillowapps.liqear.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.pillowapps.liqear.LBApplication;

//todo DI
public class AuthorizationInfoManager {
    public static final String VK_PREFERENCES = "vk_preferences_v2";
    public static final String LASTFM_PREFERENCES = "lastfm_preferences";
    public static final String ADDITIONAL_PREFERENCES = "additional_preferences";

    private AuthorizationInfoManager() {
        // no-op
    }

    public static String getVkAccessToken() {
        Context context = LBApplication.getAppContext();
        SharedPreferences vkPreferences =
                context.getSharedPreferences(VK_PREFERENCES, Context.MODE_PRIVATE);
        return vkPreferences.getString("access_token", null);
    }

    public static long getVkUserId() {
        Context context = LBApplication.getAppContext();
        SharedPreferences vkPreferences =
                context.getSharedPreferences(VK_PREFERENCES, Context.MODE_PRIVATE);
        return vkPreferences.getLong("uid", -1);
    }

    public static String getLastfmName() {
        Context context = LBApplication.getAppContext();
        SharedPreferences lastfmPreferences =
                context.getSharedPreferences(LASTFM_PREFERENCES, Context.MODE_PRIVATE);
        return lastfmPreferences.getString("name", null);
    }

    public static String getLastfmKey() {
        Context context = LBApplication.getAppContext();
        SharedPreferences lastfmPreferences =
                context.getSharedPreferences(LASTFM_PREFERENCES, Context.MODE_PRIVATE);
        return lastfmPreferences.getString("key", null);
    }

    public static boolean isAuthorizedOnLastfm() {
        return getLastfmKey() != null && getLastfmName() != null;
    }

    public static boolean isAuthorizedOnVk() {
        return getVkAccessToken() != null && 0 != getVkUserId();
    }

    public static String getLastfmApiKey() {
        return "d5de674bc94e88b751606051c2570f48";
    }

    public static String getLastfmSecret() {
        return "2b1780635fa1baa06af78bd9e90ff7e3";
    }

    public static String getVkName() {
        Context context = LBApplication.getAppContext();
        SharedPreferences vkPreferences = context.getSharedPreferences(VK_PREFERENCES, Context.MODE_PRIVATE);
        return vkPreferences.getString("name", "");
    }

    public static void setVkName(String vkName) {
        Context context = LBApplication.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(VK_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putString("name", vkName).apply();
    }

    public static String getLastfmAvatar() {
        Context context = LBApplication.getAppContext();
        SharedPreferences lastfmPreferences = context.getSharedPreferences(LASTFM_PREFERENCES, Context.MODE_PRIVATE);
        return lastfmPreferences.getString("avatar", null);
    }

    public static void setLastfmAvatar(String lastfmAvatar) {
        Context context = LBApplication.getAppContext();
        SharedPreferences lastfmPreferences = context.getSharedPreferences(LASTFM_PREFERENCES, Context.MODE_PRIVATE);
        lastfmPreferences.edit().putString("avatar", lastfmAvatar).apply();
    }

    public static String getVkAvatar() {
        Context context = LBApplication.getAppContext();
        SharedPreferences preferences =
                context.getSharedPreferences(VK_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString("avatar", null);
    }

    public static void setVkAvatar(String avatarUrl) {
        Context context = LBApplication.getAppContext();
        SharedPreferences preferences =
                context.getSharedPreferences(VK_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putString("avatar", avatarUrl).apply();
    }

    public static void signOutVk() {
        Context context = LBApplication.getAppContext();
        context.getSharedPreferences(VK_PREFERENCES,
                Context.MODE_PRIVATE).edit().clear().commit();
        skipAuth();
    }

    public static void signOutLastfm() {
        Context context = LBApplication.getAppContext();
        context.getSharedPreferences(LASTFM_PREFERENCES,
                Context.MODE_PRIVATE).edit().clear().commit();
        skipAuth();
    }

    public static void skipAuth() {
        Context context = LBApplication.getAppContext();
        context.getSharedPreferences(ADDITIONAL_PREFERENCES,
                Context.MODE_PRIVATE).edit().putBoolean(Constants.SKIP_AUTH, true).commit();
    }

    public static boolean isAuthSkipped() {
        Context context = LBApplication.getAppContext();
        return context.getSharedPreferences(ADDITIONAL_PREFERENCES,
                Context.MODE_PRIVATE).getBoolean(Constants.SKIP_AUTH, false);
    }

    public static boolean isAuthScreenNeeded() {
        return (!AuthorizationInfoManager.isAuthorizedOnVk()
                || !AuthorizationInfoManager.isAuthorizedOnLastfm())
                && !AuthorizationInfoManager.isAuthSkipped();
    }
}

package com.pillowapps.liqear.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

public class AuthorizationInfoManager {
    public static final String VK_PREFERENCES = "vk_preferences_v2";
    public static final String LASTFM_PREFERENCES = "lastfm_preferences";
    public static final String ADDITIONAL_PREFERENCES = "additional_preferences";

    private Context context;

    @Inject
    public AuthorizationInfoManager(Context context) {
        this.context = context;
    }

    public String getVkAccessToken() {
        SharedPreferences vkPreferences =
                context.getSharedPreferences(VK_PREFERENCES, Context.MODE_PRIVATE);
        return vkPreferences.getString("access_token", null);
    }

    public long getVkUserId() {
        SharedPreferences vkPreferences =
                context.getSharedPreferences(VK_PREFERENCES, Context.MODE_PRIVATE);
        return vkPreferences.getLong("uid", -1);
    }

    public String getLastfmName() {
        SharedPreferences lastfmPreferences =
                context.getSharedPreferences(LASTFM_PREFERENCES, Context.MODE_PRIVATE);
        return lastfmPreferences.getString("name", null);
    }

    public String getLastfmKey() {
        SharedPreferences lastfmPreferences =
                context.getSharedPreferences(LASTFM_PREFERENCES, Context.MODE_PRIVATE);
        return lastfmPreferences.getString("key", null);
    }

    public boolean isAuthorizedOnLastfm() {
        return getLastfmKey() != null && getLastfmName() != null;
    }

    public boolean isAuthorizedOnVk() {
        return getVkAccessToken() != null && 0 != getVkUserId();
    }

    public String getLastfmApiKey() {
        return "d5de674bc94e88b751606051c2570f48";
    }

    public String getLastfmSecret() {
        return "2b1780635fa1baa06af78bd9e90ff7e3";
    }

    public String getVkName() {
        SharedPreferences vkPreferences = context.getSharedPreferences(VK_PREFERENCES, Context.MODE_PRIVATE);
        return vkPreferences.getString("name", "");
    }

    public void setVkName(String vkName) {
        SharedPreferences preferences = context.getSharedPreferences(VK_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putString("name", vkName).apply();
    }

    public String getLastfmAvatar() {
        SharedPreferences lastfmPreferences = context.getSharedPreferences(LASTFM_PREFERENCES, Context.MODE_PRIVATE);
        return lastfmPreferences.getString("avatar", null);
    }

    public void setLastfmAvatar(String lastfmAvatar) {
        SharedPreferences lastfmPreferences = context.getSharedPreferences(LASTFM_PREFERENCES, Context.MODE_PRIVATE);
        lastfmPreferences.edit().putString("avatar", lastfmAvatar).apply();
    }

    public String getVkAvatar() {
        SharedPreferences preferences =
                context.getSharedPreferences(VK_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString("avatar", null);
    }

    public void setVkAvatar(String avatarUrl) {
        SharedPreferences preferences =
                context.getSharedPreferences(VK_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putString("avatar", avatarUrl).apply();
    }

    public void signOutVk() {
        context.getSharedPreferences(VK_PREFERENCES,
                Context.MODE_PRIVATE).edit().clear().commit();
        skipAuth();
    }

    public void signOutLastfm() {
        context.getSharedPreferences(LASTFM_PREFERENCES,
                Context.MODE_PRIVATE).edit().clear().commit();
        skipAuth();
    }

    public void skipAuth() {
        context.getSharedPreferences(ADDITIONAL_PREFERENCES,
                Context.MODE_PRIVATE).edit().putBoolean(Constants.SKIP_AUTH, true).commit();
    }

    public boolean isAuthSkipped() {
        return context.getSharedPreferences(ADDITIONAL_PREFERENCES,
                Context.MODE_PRIVATE).getBoolean(Constants.SKIP_AUTH, false);
    }

    public boolean isAuthScreenNeeded() {
        boolean authorizedBoth = isAuthorizedOnVk() && isAuthorizedOnLastfm();
        return !authorizedBoth && !isAuthSkipped();
    }
}

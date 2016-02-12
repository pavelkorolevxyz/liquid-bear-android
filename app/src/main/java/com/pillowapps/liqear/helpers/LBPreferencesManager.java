package com.pillowapps.liqear.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.pillowapps.liqear.entities.RestoreData;

public class LBPreferencesManager {

    private Context context;

    public LBPreferencesManager(Context context) {
        this.context = context;
    }

    public boolean isShakeEnabled() {
        return SharedPreferencesManager.getPreferences(context).getBoolean("shake_next", false);
    }

    public boolean isContinueFromLastPositionEnabled() {
        return SharedPreferencesManager.getPreferences(context).getBoolean("continue_from_position", true);
    }

    public boolean isToastTrackNotificationEnabled() {
        return SharedPreferencesManager.getPreferences(context).getBoolean(Constants.SHOW_TOAST_TRACK_CHANGE, false);
    }

    public boolean isNowplayingEnabled() {
        return SharedPreferencesManager.getPreferences(context).getBoolean("nowplaying_check_box_preferences", true);
    }

    public boolean isTimerActionPause() {
        return SharedPreferencesManager.getPreferences(context).getString("timer_action", "1").equals("1");
    }

    public int getPercentsToScrobble() {
        return SharedPreferencesManager.getPreferences(context).getInt("scrobble_time_preferences", 40);
    }

    public boolean isScrobblingEnabled() {
        return SharedPreferencesManager.getPreferences(context).getBoolean("scrobble_check_box_preferences", false);
    }

    public boolean isDownloadImagesEnabled() {
        return SharedPreferencesManager.getPreferences(context).getBoolean("download_images_check_box_preferences", true);
    }

    public int getPageSize() {
        return SharedPreferencesManager.getPreferences(context).getInt("page_size", 50);
    }

    public boolean isVkAddSlow() {
        return SharedPreferencesManager.getPreferences(context).getBoolean("add_to_vk_slow", true);
    }

    public RestoreData restore() {
        SharedPreferences preferences = SharedPreferencesManager.getPreferences(context);
        String artist = preferences.getString(Constants.ARTIST, "");
        String title = preferences.getString(Constants.TITLE, "");
        int currentIndex = preferences.getInt(Constants.CURRENT_INDEX, 0);
        int position = preferences.getInt(Constants.CURRENT_POSITION, 0);
        return new RestoreData(artist, title, currentIndex, position);
    }
}

package com.pillowapps.liqear.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pillowapps.liqear.R;

public class PreferencesScreenManager {

    public static final String SHAKE_NEXT = "shake_next";
    public static final String CONTINUE_FROM_POSITION = "continue_from_position";
    public static final String SHOW_TRACK_CHANGE_TOAST = "show_track_change_toast";
    public static final String NOWPLAYING_CHECK_BOX_PREFERENCES = "nowplaying_check_box_preferences";
    public static final String NOWPLAYING_VK_CHECK_BOX_PREFERENCES = "nowplaying_vk_check_box_preferences";
    public static final String TIMER_ACTION = "timer_action";
    public static final String SCROBBLE_TIME_PREFERENCES = "scrobble_time_preferences";
    public static final String SCROBBLE_CHECK_BOX_PREFERENCES = "scrobble_check_box_preferences";
    public static final String DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES = "download_images_check_box_preferences";
    public static final String PAGE_SIZE = "page_size";
    public static final String ADD_TO_VK_SLOW = "add_to_vk_slow";
    public static final String LUCKY_SEARCH_CHECK_BOX_PREFERENCES = "lucky_search_check_box_preferences";
    public static final String SHOW_IMAGES_GRID = "show_images_grid";

    private Context context;
    private SharedPreferences preferences;

    public PreferencesScreenManager(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isShakeEnabled() {
        return preferences.getBoolean(SHAKE_NEXT, false);
    }

    public boolean isContinueFromLastPositionEnabled() {
        return preferences.getBoolean(CONTINUE_FROM_POSITION, true);
    }

    public boolean isToastTrackNotificationEnabled() {
        return preferences.getBoolean(SHOW_TRACK_CHANGE_TOAST, false);
    }

    public boolean isNowplayingLastfmEnabled() {
        return preferences.getBoolean(NOWPLAYING_CHECK_BOX_PREFERENCES, true);
    }

    public boolean isNowplayingVkEnabled() {
        return preferences.getBoolean(NOWPLAYING_VK_CHECK_BOX_PREFERENCES, true);
    }

    public boolean isTimerActionPause() {
        return preferences.getString(TIMER_ACTION, "1").equals("1");
    }

    public int getPercentsToScrobble() {
        return preferences.getInt(SCROBBLE_TIME_PREFERENCES, 40);
    }

    public boolean isScrobblingEnabled() {
        return preferences.getBoolean(SCROBBLE_CHECK_BOX_PREFERENCES, false);
    }

    public boolean isDownloadImagesEnabled() {
        return preferences.getBoolean(DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true);
    }

    public int getPageSize() {
        return preferences.getInt(PAGE_SIZE, 50);
    }

    public boolean isVkAddSlow() {
        return preferences.getBoolean(ADD_TO_VK_SLOW, true);
    }

    public boolean isLuckySearchEnabled() {
        return preferences.getBoolean(LUCKY_SEARCH_CHECK_BOX_PREFERENCES, true);
    }

    public boolean isShowImagesAsGridEnabled() {
        return preferences.getBoolean(SHOW_IMAGES_GRID, true);
    }

    public String getShareFormat() {
        return preferences.getString(Constants.SHARE_FORMAT, context.getString(R.string.listening_now));
    }
}

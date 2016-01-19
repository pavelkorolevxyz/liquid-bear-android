package com.pillowapps.liqear.helpers;

public class LBPreferencesManager {

    private LBPreferencesManager() {
        // no-op
    }

    public static boolean isShakeEnabled() {
        return SharedPreferencesManager.getPreferences().getBoolean("shake_next", false);
    }

    public static boolean isContinueFromLastPositionEnabled() {
        return SharedPreferencesManager.getPreferences().getBoolean("continue_from_position", true);
    }

    public static boolean isToastTrackNotificationEnabled() {
        return SharedPreferencesManager.getPreferences().getBoolean(Constants.SHOW_TOAST_TRACK_CHANGE, false);
    }

    public static boolean isNowplayingEnabled() {
        return SharedPreferencesManager.getPreferences().getBoolean("nowplaying_check_box_preferences", true);
    }

    public static boolean isTimerActionPause() {
        return SharedPreferencesManager.getPreferences().getString("timer_action", "1").equals("1");
    }

    public static int getPercentsToScrobble() {
        return SharedPreferencesManager.getPreferences().getInt("scrobble_time_preferences", 40);
    }

    public static boolean isScrobblingEnabled() {
        return SharedPreferencesManager.getPreferences().getBoolean("scrobble_check_box_preferences", false);
    }

    public static boolean isDownloadImagesEnabled() {
        return SharedPreferencesManager.getPreferences().getBoolean("download_images_check_box_preferences", true);
    }

    public static int getPageSize() {
        return SharedPreferencesManager.getPreferences().getInt("page_size", 50);
    }

    public static boolean isVkAddSlow() {
        return SharedPreferencesManager.getPreferences().getBoolean("add_to_vk_slow", true);
    }
}

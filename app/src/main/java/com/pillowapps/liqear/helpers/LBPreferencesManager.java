package com.pillowapps.liqear.helpers;

public class LBPreferencesManager {

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
}

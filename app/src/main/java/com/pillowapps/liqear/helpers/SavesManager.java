package com.pillowapps.liqear.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SavesManager {

    public static final String CURRENT_POSITION = "current_position";
    public static final String CURRENT_BUFFER = "current_buffer";
    public static final String CURRENT_INDEX = "current_index";
    public static final String SHUFFLE_MODE_ON = "shuffle_mode_on";
    public static final String REPEAT_MODE_ON = "repeat_mode_on";
    public static final String TIMER_DEFAULT = "timer_default";

    public static final String ARTIST = "artist";
    public static final String TITLE = "title";
    public static final String DURATION = "duration";

    private Context context;
    private SharedPreferences preferences;

    public SavesManager(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @return minutes
     */
    public int getTimerDefault() {
        return preferences.getInt(TIMER_DEFAULT, 10);
    }

    public void saveTimerDefault(int minutes) {
        preferences.edit().putInt(TIMER_DEFAULT, minutes).apply();
    }

    public void saveCurrentPosition(int currentPosition) {
        preferences.edit().putInt(CURRENT_POSITION, currentPosition).apply();
    }

    public void saveBuffer(int currentBuffer) {
        preferences.edit().putInt(CURRENT_BUFFER, currentBuffer).apply();
    }

    public void saveShuffleMode(boolean shuffleOn) {
        preferences.edit().putBoolean(SHUFFLE_MODE_ON, shuffleOn).apply();
    }

    public void saveRepeatMode(boolean repeatOn) {
        preferences.edit().putBoolean(REPEAT_MODE_ON, repeatOn).apply();
    }

    public void saveCurrentIndex(int index) {
        preferences.edit().putInt(CURRENT_INDEX, index).apply();
    }

    public void saveArtist(String artist) {
        preferences.edit().putString(ARTIST, artist).apply();
    }

    public void saveTitle(String title) {
        preferences.edit().putString(TITLE, title).apply();
    }

    public void saveDuration(int duration) {
        preferences.edit().putInt(DURATION, duration).apply();
    }

    public String getArtist() {
        return preferences.getString(ARTIST, "");
    }

    public String getTitle() {
        return preferences.getString(TITLE, "");
    }

    public int getCurrentIndex() {
        return preferences.getInt(CURRENT_INDEX, 0);
    }

    public int getPosition() {
        return preferences.getInt(CURRENT_POSITION, 0);
    }

    public void toggleTimeInversion() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.TIME_INVERTED, !preferences.getBoolean(Constants.TIME_INVERTED, false));
        editor.apply();
    }

    public boolean isTimeInverted() {
        return preferences.getBoolean(Constants.TIME_INVERTED, false);
    }
}

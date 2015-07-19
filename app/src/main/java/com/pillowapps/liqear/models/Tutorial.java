package com.pillowapps.liqear.models;

import android.content.SharedPreferences;

import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;

public class Tutorial {
    private SharedPreferences startPreferences = SharedPreferencesManager.getStartPreferences();
    private SharedPreferences.Editor editor = startPreferences.edit();

    public void end() {
        editor.putBoolean(Constants.TUTORIAL_DISABLED, true).apply();
    }

    public boolean isEnabled() {
        return !startPreferences.getBoolean(Constants.TUTORIAL_DISABLED, false);
    }
}

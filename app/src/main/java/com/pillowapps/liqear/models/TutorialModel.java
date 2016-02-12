package com.pillowapps.liqear.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;

public class TutorialModel {
    private SharedPreferences startPreferences;

    public TutorialModel(Context context) {
        startPreferences = SharedPreferencesManager.getStartPreferences(context);
    }

    public void disableTutorial() {
        startPreferences.edit().putBoolean(Constants.TUTORIAL_DISABLED, true).apply();
    }

    public boolean isEnabled() {
        return !startPreferences.getBoolean(Constants.TUTORIAL_DISABLED, false);
    }
}

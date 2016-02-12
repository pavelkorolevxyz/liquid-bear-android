package com.pillowapps.liqear.helpers;

import android.content.Context;

import com.pillowapps.liqear.R;

import javax.inject.Inject;

public class PreferencesModel {

    private Context context;
    private PreferencesScreenManager preferencesScreenManager;

    @Inject
    public PreferencesModel(Context context, PreferencesScreenManager preferencesScreenManager) {
        this.context = context;
        this.preferencesScreenManager = preferencesScreenManager;
    }

    public String getShareTemplate() {
        return preferencesScreenManager.getShareFormat();
    }
}

package com.pillowapps.liqear.helpers;

import android.content.Context;

import com.pillowapps.liqear.R;

import javax.inject.Inject;

public class PreferencesModel {

    private Context context;

    @Inject
    public PreferencesModel(Context context) {
        this.context = context;
    }

    public String getShareTemplate() {
        return SharedPreferencesManager.getPreferences(context).getString(Constants.SHARE_FORMAT,
                context.getString(R.string.listening_now)
        );
    }
}

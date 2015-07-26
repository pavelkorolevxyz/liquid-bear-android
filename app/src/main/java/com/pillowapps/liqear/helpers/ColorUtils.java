package com.pillowapps.liqear.helpers;

import android.content.Context;
import android.content.res.Resources;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;

public class ColorUtils {

    public static int getColorForMatch(double match) {
        Context context = LBApplication.getAppContext();
        long percent = Math.round(match * 100);
        Resources resources = context.getResources();
        if (percent > 0 && percent < 20) {
            return resources.getColor(R.color.match_20);
        } else if (percent >= 20 && percent < 40) {
            return resources.getColor(R.color.match_40);
        } else if (percent >= 40 && percent < 60) {
            return resources.getColor(R.color.match_60);
        } else if (percent >= 60 && percent < 80) {
            return resources.getColor(R.color.match_80);
        } else if (percent >= 80 && percent <= 100) {
            return resources.getColor(R.color.match_100);
        } else {
            return resources.getColor(R.color.match_0);
        }
    }

}

package com.pillowapps.liqear.helpers;

import android.content.Context;
import android.content.pm.PackageManager;

import com.pillowapps.liqear.LBApplication;

public class AppUtils {

    private AppUtils() {
        // no-op
    }

    public static String getAppVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();
            return packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "undefined";
        }
    }
}

package com.pillowapps.liqear.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    public static String secondsToMinuteString(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    public static String getCurrentTimeInSeconds() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    public static String formatMillisForFileName(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyHHmmss", Locale.US);
        return format.format(date);
    }
}

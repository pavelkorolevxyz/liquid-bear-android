package com.pillowapps.liqear.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd.MM.yyyy", Locale.UK);

    private DateUtils() {
        // no-op
    }

    public static String formatDate(Date date) {
        return FORMATTER.format(date);
    }
}

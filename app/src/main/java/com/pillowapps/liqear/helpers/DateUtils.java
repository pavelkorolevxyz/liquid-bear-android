package com.pillowapps.liqear.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.UK);

    public static String formatDate(Date date) {
        return formatter.format(date);
    }
}

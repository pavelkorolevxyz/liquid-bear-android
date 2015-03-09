package com.pillowapps.liqear.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

    public static String formatDate(Date date) {
        return formatter.format(date);
    }
}

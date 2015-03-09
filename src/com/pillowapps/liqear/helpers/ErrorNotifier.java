package com.pillowapps.liqear.helpers;

import android.app.Activity;
import android.app.AlertDialog;

public class ErrorNotifier {

    public static void showError(Activity activity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

}

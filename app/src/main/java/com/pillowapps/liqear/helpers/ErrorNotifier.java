package com.pillowapps.liqear.helpers;

import android.app.Activity;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pillowapps.liqear.LBApplication;

public class ErrorNotifier {

    private ErrorNotifier() {
        // no-op
    }

    public static void showError(Activity activity, String message) {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .content(message)
                .positiveText(android.R.string.ok)
                .build();

        try {
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(LBApplication.getAppContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

}

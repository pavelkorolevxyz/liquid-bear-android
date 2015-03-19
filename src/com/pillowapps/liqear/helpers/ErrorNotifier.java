package com.pillowapps.liqear.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Toast;

import com.pillowapps.liqear.LBApplication;

public class ErrorNotifier {

    public static void showError(Activity activity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        try {
            builder.show();
        } catch (Exception e) {
            Toast.makeText(LBApplication.getAppContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

}

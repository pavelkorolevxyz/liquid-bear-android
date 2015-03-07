package com.pillowapps.liqear.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.activities.UserViewerVkActivity;
import com.pillowapps.liqear.entities.vk.VkError;

import retrofit.RetrofitError;

public class ErrorNotifier {
    public static void showLastfmError(RetrofitError error) {
        Context context = LiqearApplication.getAppContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Object body = error.getBody();
        String message;
        if (body != null) {
            message = body.toString();
        } else {
            message = error.getMessage();
        }
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    public static void showLastfmError(Activity activity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    public static void showVkError(UserViewerVkActivity activity, VkError error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String message = error.getErrorMessage();
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }
}

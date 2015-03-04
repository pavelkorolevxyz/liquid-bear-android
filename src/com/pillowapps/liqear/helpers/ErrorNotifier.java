package com.pillowapps.liqear.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.activity.UserViewerVkActivity;
import com.pillowapps.liqear.models.vk.VkError;

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

    public static void showLastfmError(Activity activity, RetrofitError error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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

    public static void showVkError(UserViewerVkActivity activity, VkError error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String message = error.getErrorMessage();
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }
}

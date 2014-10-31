package com.pillowapps.liqear.helpers;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.audio.AudioTimeline;
import com.pillowapps.liqear.connection.Params;
import com.pillowapps.liqear.connection.ReadyResult;
import com.pillowapps.liqear.models.ErrorResponseLastfm;
import com.pillowapps.liqear.models.ErrorResponseVk;
import com.pillowapps.liqear.models.Track;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static int getShuffleButtonImage() {
        switch (AudioTimeline.getShuffleMode()) {
            case SHUFFLE:
                return R.drawable.shuffle_active;
            case DEFAULT:
                return R.drawable.shuffle_normal;
            default:
                break;
        }
        return R.drawable.shuffle_normal;
    }

    public static int getRepeatButtonImage() {
        switch (AudioTimeline.getRepeatMode()) {
            case REPEAT_PLAYLIST:
                return R.drawable.repeat_normal;
            case REPEAT:
                return R.drawable.repeat_active;
            default:
                break;
        }
        return R.drawable.repeat_normal;
    }

    public static String secondsToString(final int timeInSeconds) {
        int seconds = timeInSeconds % 60;
        if (seconds < 10) {
            return (timeInSeconds / 60) + ":0" + seconds;
        } else {
            return (timeInSeconds / 60) + ":" + seconds;
        }
    }

    public static String getCurrentTime() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) LiqearApplication.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static void showErrorDialog(ReadyResult result, Context context, Params.ApiSource apiSource) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String title = "";
        String message = "";
        int icon = android.R.drawable.ic_dialog_info;
        switch (apiSource) {
            case VK:
                ErrorResponseVk errorResponseVk = (ErrorResponseVk) result.getObject();
                if (errorResponseVk.getErrorCode() == Constants.CAPTCHA_NEEDED) {
                    // TODO captcha download and response query
                }
                message = errorResponseVk.getErrorMessage();
                title = errorResponseVk.getTitle();
                icon = R.drawable.vk_logo;
                break;
            case LASTFM:
                ErrorResponseLastfm errorResponseLastfm = (ErrorResponseLastfm) result.getObject();
                message = errorResponseLastfm.getMessage();
                title = errorResponseLastfm.getTitle();
                icon = R.drawable.lastfm_logo;
                break;
            default:
                break;
        }
        builder.setMessage(message)
                .setCancelable(true)
                .setTitle(title)
                .setIcon(icon)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog alert = builder.create();
        try {
            alert.show();
        } catch (Exception e) {
            Toast.makeText(context, "(" + title + ")" + message, Toast.LENGTH_LONG).show();
        }
    }

    public static String formatMillisToFileName(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyHHmmss");
        return format.format(date);
    }

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static int getLoveButtonImage() {
        Track track = AudioTimeline.getCurrentTrack();
        if (track == null) return R.drawable.love_not_active;
        return track.isLoved() ? R.drawable.love_clicked : R.drawable.love_not_active;
    }
}

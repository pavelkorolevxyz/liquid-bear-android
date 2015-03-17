package com.pillowapps.liqear.helpers;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.audio.AudioTimeline;
import com.pillowapps.liqear.entities.Track;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static int getShuffleButtonImage() {
        switch (AudioTimeline.getShuffleMode()) {
            case SHUFFLE:
                return R.drawable.ic_shuffle_active;
            case DEFAULT:
                return R.drawable.ic_shuffle;
            default:
                break;
        }
        return R.drawable.ic_shuffle;
    }

    public static int getRepeatButtonImage() {
        switch (AudioTimeline.getRepeatMode()) {
            case REPEAT_PLAYLIST:
                return R.drawable.ic_repeat;
            case REPEAT:
                return R.drawable.ic_repeat_active;
            default:
                break;
        }
        return R.drawable.ic_repeat;
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
        if (track == null) return R.drawable.ic_love;
        return track.isLoved() ? R.drawable.ic_love_active : R.drawable.ic_love;
    }
}

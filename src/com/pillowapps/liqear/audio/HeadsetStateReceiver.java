package com.pillowapps.liqear.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import com.pillowapps.liqear.global.Config;

public class HeadsetStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            AudioTimeline.getMusicPlaybackService().setPlayOnPrepared(false);
            AudioTimeline.getMusicPlaybackService().pause(true);
            Intent intentPause = new Intent();
            intentPause.setAction(Config.ACTION_SERVICE);
            intentPause.putExtra("callback-type", MusicPlaybackService.PAUSE_CALLBACK);
        }
    }

}

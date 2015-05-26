package com.pillowapps.liqear.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class HeadsetStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            context.startService(new Intent(context, MusicService.class)
                    .setAction(MusicService.ACTION_PAUSE));
        }
    }

}

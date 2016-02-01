package com.pillowapps.liqear.audio;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.CompatIcs;

import javax.inject.Inject;

public class RemoteControlManager {

    private Context context;

    private Timeline timeline;

    private AudioManager audioManager;

    @Inject
    public RemoteControlManager(Context context, Timeline timeline, AudioManager audioManager) {
        this.context = context;
        this.timeline = timeline;
        this.audioManager = audioManager;
    }

    public void register() {
        final Track currentTrack = timeline.getCurrentTrack();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            CompatIcs.registerRemote(context, audioManager);
            if (currentTrack != null) {
                CompatIcs.updateRemote(context, currentTrack);
            }
        } else {
            MediaButtonReceiver.registerMediaButton(context);
        }
    }

    public void unregister() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            CompatIcs.unregisterRemote(context, audioManager);
        } else {
            MediaButtonReceiver.unregisterMediaButton(context);
        }
    }
}

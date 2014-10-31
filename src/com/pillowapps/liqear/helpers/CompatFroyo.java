package com.pillowapps.liqear.helpers;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.media.AudioManager;import java.lang.Override;

/**
 * Framework methods only in Froyo or above go here.
 */
@TargetApi(8)
public class CompatFroyo implements AudioManager.OnAudioFocusChangeListener {
    public static void registerMediaButtonEventReceiver(AudioManager manager,
                                                        ComponentName receiver) {
        manager.registerMediaButtonEventReceiver(receiver);
    }

    public static void unregisterMediaButtonEventReceiver(AudioManager manager,
                                                          ComponentName receiver) {
        manager.unregisterMediaButtonEventReceiver(receiver);
    }

    private static CompatFroyo sAudioFocus;

    public static void createAudioFocus()
    {
        sAudioFocus = new CompatFroyo();
    }

    public static void requestAudioFocus(AudioManager manager)
    {
        manager.requestAudioFocus(sAudioFocus, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public void onAudioFocusChange(int type)
    {
    }
}
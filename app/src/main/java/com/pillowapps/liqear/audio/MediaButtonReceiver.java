package com.pillowapps.liqear.audio;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import com.pillowapps.liqear.helpers.CompatFroyo;

public class MediaButtonReceiver extends BroadcastReceiver {
    private static final int DOUBLE_CLICK_DELAY = 400;
    private static int sInCall = -1;
    private static long sLastClickTime = 0;

    private static boolean isInCall(Context context) {
        if (sInCall == -1) {
            TelephonyManager manager = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            sInCall = (byte) (manager.getCallState() == TelephonyManager.CALL_STATE_IDLE ? 0 : 1);
        }
        return sInCall == 1;
    }

    public static boolean processKey(Context context, KeyEvent event) {
        if (event == null || isInCall(context)) {
            return false;
        }
        Intent intent = null;
        int action = event.getAction();
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_HEADSETHOOK:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (action == KeyEvent.ACTION_DOWN) {
                    long time = SystemClock.uptimeMillis();
                    if (time - sLastClickTime < DOUBLE_CLICK_DELAY) {
                        intent = new Intent(context, MusicService.class)
                                .setAction(MusicService.ACTION_NEXT);
                    } else {
                        intent = new Intent(context, MusicService.class)
                                .setAction(MusicService.ACTION_PLAY_PAUSE);
                    }
                    sLastClickTime = time;
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                if (action == KeyEvent.ACTION_DOWN) {
                    intent = new Intent(context, MusicService.class)
                            .setAction(MusicService.ACTION_NEXT);
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                if (action == KeyEvent.ACTION_DOWN) {
                    intent = new Intent(context, MusicService.class)
                            .setAction(MusicService.ACTION_PREV);
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (action == KeyEvent.ACTION_DOWN) {
                    intent = new Intent(context, MusicService.class)
                            .setAction(MusicService.ACTION_PLAY);
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (action == KeyEvent.ACTION_DOWN) {
                    intent = new Intent(context, MusicService.class)
                            .setAction(MusicService.ACTION_PAUSE);
                }
                break;
            default:
                return false;
        }
        if (intent != null) context.startService(intent);

        return true;
    }

    public static void registerMediaButton(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ComponentName receiver = new ComponentName(context.getPackageName(),
                MediaButtonReceiver.class.getName());
        CompatFroyo.registerMediaButtonEventReceiver(audioManager, receiver);
    }

    public static void unregisterMediaButton(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ComponentName receiver = new ComponentName(context.getPackageName(),
                MediaButtonReceiver.class.getName());
        CompatFroyo.unregisterMediaButtonEventReceiver(audioManager, receiver);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            boolean handled = processKey(context, event);
            if (handled && isOrderedBroadcast())
                abortBroadcast();
        }
    }
}

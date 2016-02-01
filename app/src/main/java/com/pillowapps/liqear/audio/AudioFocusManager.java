package com.pillowapps.liqear.audio;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.entities.PlayingState;
import com.pillowapps.liqear.helpers.CompatIcs;

public class AudioFocusManager {

    private Context context;

    private Timeline timeline;

    private AudioManager audioManager;

    private MediaPlayerManager mediaPlayerManager;

    private HeadsetStateReceiver headsetStateReceiver;

    private AudioManager.OnAudioFocusChangeListener focusChangeListener;
    private PhoneStateListener phoneStateListener;

    public void init() {
        headsetStateReceiver = new HeadsetStateReceiver();

        IntentFilter receiverFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        LBApplication.getAppContext().registerReceiver(headsetStateReceiver, receiverFilter);

        focusChangeListener = focusChange -> {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                if (timeline.getPlayingState() == PlayingState.PLAYING) {
                    mediaPlayerManager.pause();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        CompatIcs.unregisterRemote(context, audioManager);
                    } else {
                        MediaButtonReceiver.unregisterMediaButton(context);
                    }
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (timeline.getPlayingStateBeforeCall() == PlayingState.PLAYING) {
                    mediaPlayerManager.play();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    CompatIcs.registerRemote(context, audioManager);
                    if (timeline.getCurrentTrack() != null) {
                        CompatIcs.updateRemote(context,
                                timeline.getCurrentTrack());
                    }
                } else {
                    MediaButtonReceiver.registerMediaButton(context);
                }
            }
        };
        audioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        registerPhoneCallReceiver();
    }

    private void registerPhoneCallReceiver() {
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        pause();
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (timeline.getPlayingStateBeforeCall() == PlayingState.PLAYING) {
                            play();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        pause();
                        break;
                    default:
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        changePhoneCallReceiverListener(PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void unregisterPhoneCallReceiver() {
        changePhoneCallReceiverListener(PhoneStateListener.LISTEN_NONE);
    }

    private void changePhoneCallReceiverListener(int listenCallState) {
        TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, listenCallState);
        }
    }

    public void abandonFocus() {
        audioManager.abandonAudioFocus(focusChangeListener);
    }
}

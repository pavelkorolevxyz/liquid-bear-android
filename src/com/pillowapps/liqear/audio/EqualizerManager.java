package com.pillowapps.liqear.audio;

import android.media.audiofx.BassBoost;

import com.pillowapps.liqear.helpers.CompatEq;

//todo
public class EqualizerManager {
    private static CompatEq equalizer;
    private static BassBoost bassBoost;

    public static CompatEq getEqualizer() {
//        if (AudioTimeline.getMusicPlaybackService() == null
//                || AudioTimeline.getMusicPlaybackService().getMediaPlayer() == null) {
//            return null;
//        }
//        equalizer = new CompatEq(AudioTimeline.getMusicPlaybackService().getMediaPlayer());
        return equalizer;
    }

    public static BassBoost getBassBoost() {
//        if (AudioTimeline.getMusicPlaybackService() == null
//                || AudioTimeline.getMusicPlaybackService().getMediaPlayer() == null) {
//            return null;
//        }
//        int audioSessionId = AudioTimeline.getMusicPlaybackService()
//                .getMediaPlayer().getAudioSessionId();
//        bassBoost = new BassBoost(0, audioSessionId);
        return bassBoost;
    }

    public static void releaseEqualizer() {
        try {
            equalizer.release();
            bassBoost.release();
        } catch (Exception ignored) {
        }
    }

    public static void restoreEqualizer() {
//        CompatEq mEqualizer = getEqualizer();
//        getBassBoost();
//        mEqualizer.setEnabled(true);
//        short bands = mEqualizer.getNumberOfBands();
//        SharedPreferences preferences = PreferencesManager.getEqualizerPreferences();
//        try {
//            for (short i = 0; i < bands; i++) {
//                mEqualizer.setBandLevel(i, (short) preferences.getInt(Constants.EQUALIZER + i, 0));
//            }
//            bassBoost.setStrength((short) preferences.getInt(Constants.EQUALIZER_BASS, 0));
//        } catch (Exception ignored) {
//            ignored.printStackTrace();
//        }
    }

    public static void setEnabled(boolean enabled) {
//        if (equalizer != null) {
//            equalizer.setEnabled(enabled);
//        }
//        try {
//            bassBoost.setEnabled(enabled);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}

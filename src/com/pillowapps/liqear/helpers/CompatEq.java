package com.pillowapps.liqear.helpers;

import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;

/**
 * Gingerbread equalizer compatibility.
 */
@TargetApi(9)
public class CompatEq {
    private final Equalizer mEq;

    /**
     * Create the equalizer and attach it to the given MediaPlayer's audio
     * session.
     */
    public CompatEq(MediaPlayer player) {
        Equalizer eq = new Equalizer(0, player.getAudioSessionId());
        mEq = eq;
    }

    /**
     * Call {@link Equalizer#getNumberOfBands()}
     */
    public short getNumberOfBands() {
        return mEq.getNumberOfBands();
    }

    /**
     * Call {@link Equalizer#setBandLevel(short, short)}.
     */
    public void setBandLevel(short band, short level) {
        mEq.setBandLevel(band, level);
    }

    public void setEnabled(boolean enabled) {
        mEq.setEnabled(enabled);
    }

    public short getNumberOfPresets() {
        return mEq.getNumberOfPresets();
    }

    public String getPresetName(short i) {
        return mEq.getPresetName(i);
    }

    public void usePreset(short position) {
        mEq.usePreset(position);
    }

    public int getBandLevel(short i) {
        return mEq.getBandLevel(i);
    }

    public short[] getBandLevelRange() {
        return mEq.getBandLevelRange();
    }

    public int getCenterFreq(short band) {
        return mEq.getCenterFreq(band);
    }

    public void release() {
        mEq.release();
    }
}

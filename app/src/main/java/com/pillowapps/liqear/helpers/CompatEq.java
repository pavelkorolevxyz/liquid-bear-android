package com.pillowapps.liqear.helpers;

import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;

public class CompatEq {
    private final Equalizer equalizer;

    public CompatEq(MediaPlayer player) {
        equalizer = new Equalizer(0, player.getAudioSessionId());
    }

    public short getNumberOfBands() {
        return equalizer.getNumberOfBands();
    }

    public void setBandLevel(short band, short level) {
        equalizer.setBandLevel(band, level);
    }

    public void setEnabled(boolean enabled) {
        equalizer.setEnabled(enabled);
    }

    public short getNumberOfPresets() {
        return equalizer.getNumberOfPresets();
    }

    public String getPresetName(short i) {
        return equalizer.getPresetName(i);
    }

    public void usePreset(short position) {
        equalizer.usePreset(position);
    }

    public int getBandLevel(short i) {
        return equalizer.getBandLevel(i);
    }

    public short[] getBandLevelRange() {
        return equalizer.getBandLevelRange();
    }

    public int getCenterFreq(short band) {
        return equalizer.getCenterFreq(band);
    }

    public void release() {
        equalizer.release();
    }
}

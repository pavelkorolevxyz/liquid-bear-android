package com.pillowapps.liqear.audio.player;

public class PlaybackInfo {
    private long currentPositionInMillis;
    private long durationInMillis;
    private long bufferizationInMillis;

    public PlaybackInfo(long currentPositionInMillis, long durationInMillis, long bufferizationInMillis) {
        this.currentPositionInMillis = currentPositionInMillis;
        this.durationInMillis = durationInMillis;
        this.bufferizationInMillis = bufferizationInMillis;
    }

    public long getCurrentPositionInMillis() {
        return currentPositionInMillis;
    }

    public long getDurationInMillis() {
        return durationInMillis;
    }

    public long getBufferizationInMillis() {
        return bufferizationInMillis;
    }

    @Override
    public String toString() {
        return "PlaybackInfo{" +
                "currentPositionInMillis=" + currentPositionInMillis +
                ", durationInMillis=" + durationInMillis +
                ", bufferizationInMillis=" + bufferizationInMillis +
                '}';
    }
}

package com.pillowapps.liqear.models;

import com.pillowapps.liqear.network.GetResponseCallback;

public class TrackUrlQuery {
    private final GetResponseCallback getResponseCallback;
    private final Track currentTrack;
    private final boolean current;
    private final int urlNumber;
    private final long timeStamp;

    public TrackUrlQuery(Track currentTrack, boolean current, int urlNumber,
                         GetResponseCallback getResponseCallback, long timeStamp) {
        this.currentTrack = currentTrack;
        this.current = current;
        this.urlNumber = urlNumber;
        this.getResponseCallback = getResponseCallback;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "TrackUrlQuery{" +
                "getResponseCallback=" + getResponseCallback +
                ", currentTrack=" + currentTrack +
                ", current=" + current +
                ", urlNumber=" + urlNumber +
                '}';
    }

    public GetResponseCallback getGetResponseCallback() {
        return getResponseCallback;
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public boolean isCurrent() {
        return current;
    }

    public int getUrlNumber() {
        return urlNumber;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}

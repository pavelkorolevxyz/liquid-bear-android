package com.pillowapps.liqear.entities.events;

import com.pillowapps.liqear.entities.Track;

public class TrackInfoEvent {

    private Track track;
    private int currentIndex;

    public TrackInfoEvent(Track track, int currentIndex) {
        this.track = track;
        this.currentIndex = currentIndex;
    }

    public Track getTrack() {
        return track;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}

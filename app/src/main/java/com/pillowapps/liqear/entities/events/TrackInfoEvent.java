package com.pillowapps.liqear.entities.events;

import com.pillowapps.liqear.entities.Track;

public class TrackInfoEvent {

    private Track track;

    public TrackInfoEvent(Track track) {
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }
}

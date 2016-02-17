package com.pillowapps.liqear.entities.events;

import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Track;

public class TrackAndAlbumInfoUpdatedEvent {
    private Album album;
    private Track track;

    public TrackAndAlbumInfoUpdatedEvent(Track track, Album album) {
        this.track = track;
        this.album = album;
    }

    public Album getAlbum() {
        return album;
    }

    public Track getTrack() {
        return track;
    }
}

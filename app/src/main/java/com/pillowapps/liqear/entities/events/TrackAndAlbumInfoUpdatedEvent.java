package com.pillowapps.liqear.entities.events;

import com.pillowapps.liqear.entities.Album;

public class TrackAndAlbumInfoUpdatedEvent {
    private Album album;

    public TrackAndAlbumInfoUpdatedEvent(Album album) {
        this.album = album;
    }

    public Album getAlbum() {
        return album;
    }
}

package com.pillowapps.liqear.entities.events;

import com.pillowapps.liqear.entities.Album;

public class AlbumInfoEvent {
    private Album album;

    public AlbumInfoEvent(Album album) {
        this.album = album;
    }

    public Album getAlbum() {
        return album;
    }
}

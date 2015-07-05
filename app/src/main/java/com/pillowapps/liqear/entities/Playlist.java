package com.pillowapps.liqear.entities;

import java.util.List;

public class Playlist {
    Long id;
    String title;
    boolean mainPlaylist = false;

    List<Track> tracks;

    public Playlist(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Playlist() {
        // No operations.
    }

    public Playlist(List<Track> tracks) {
        this.tracks = tracks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long _id) {
        this.id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public void setMainPlaylist(boolean mainPlaylist) {
        this.mainPlaylist = mainPlaylist;
    }
}

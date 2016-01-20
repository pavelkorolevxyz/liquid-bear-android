package com.pillowapps.liqear.entities;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private Long id;

    private String title;

    private List<Track> tracks = new ArrayList<>();

    private Boolean mainPlaylist;

    public Playlist(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Playlist(String title, List<Track> tracks) {
        this.title = title;
        this.tracks = tracks;
    }

    public Playlist(List<Track> tracks) {
        this.tracks = tracks;
    }

    public Playlist() {
        // No operations.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean isMainPlaylist() {
        return mainPlaylist;
    }

    public void setMainPlaylist(Boolean mainPlaylist) {
        this.mainPlaylist = mainPlaylist;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "title='" + title + '\'' +
                ", tracks=" + tracks +
                '}';
    }
}

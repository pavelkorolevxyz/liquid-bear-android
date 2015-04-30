package com.pillowapps.liqear.entities;

import java.util.List;

public class Playlist {
    private String title;
    private List<Track> tracks;
    private long pid;

    public Playlist(long pid, String title) {
        this.pid = pid;
        this.title = title;
    }

    public Playlist() {
        // No operations.
    }

    public Playlist(List<Track> tracks) {
        this.tracks = tracks;
    }

    @Override
    public String toString() {
        return "Playlist [title=" + title + ", tracks=" + tracks + ", pid=" + pid + "]";
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

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public int size() {
        if (tracks == null) return 0;
        return tracks.size();
    }
}

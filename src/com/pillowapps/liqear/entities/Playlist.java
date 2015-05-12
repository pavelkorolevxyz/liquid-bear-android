package com.pillowapps.liqear.entities;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Playlist extends RealmObject {
    @PrimaryKey
    private long pid;
    private String title;
    private RealmList<Track> tracks;
    private boolean mainPlaylist = false;

    public Playlist(long pid, String title) {
        this.pid = pid;
        this.title = title;
    }

    public Playlist() {
        // No operations.
    }

    public Playlist(List<Track> tracks) {
        RealmList<Track> realmList = new RealmList<>();
        realmList.addAll(tracks);
        this.tracks = realmList;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<Track> getTracks() {
        return tracks;
    }

    public void setTracks(RealmList<Track> tracks) {
        this.tracks = tracks;
    }

    public void setMainPlaylist(boolean mainPlaylist) {
        this.mainPlaylist = mainPlaylist;
    }

    public boolean isMainPlaylist() {
        return mainPlaylist;
    }
}

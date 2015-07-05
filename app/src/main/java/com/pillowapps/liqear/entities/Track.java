package com.pillowapps.liqear.entities;

public class Track {

    Long id;
    String title;
    String artist;
    String album;
    long ownerId;
    long audioId;
    int duration;
    boolean local = false;
    Long playlistId;

    private int realPosition;
    private String url = null;
    private boolean loved = false;
    private boolean addedToVk = false;

    public Track(long audioId, long oid) {
        this.audioId = audioId;
        this.ownerId = oid;
    }

    public Track(String artist, String title, String url, boolean local) {
        this.artist = artist;
        this.title = title;
        this.url = url;
        this.local = local;
    }

    public Track(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    public Track() {
    }

    public Track(String artist, String title, String url) {
        this.artist = artist;
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getAudioId() {
        return audioId;
    }

    public void setAudioId(long audioId) {
        this.audioId = audioId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLoved() {
        return loved;
    }

    public void setLoved(boolean loved) {
        this.loved = loved;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isAddedToVk() {
        return addedToVk;
    }

    public void setAddedToVk(boolean addedToVk) {
        this.addedToVk = addedToVk;
    }

    public int getRealPosition() {
        return realPosition;
    }

    public void setRealPosition(int realPosition) {
        this.realPosition = realPosition;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setPlaylistId(Long playlistId) {
        this.playlistId = playlistId;
    }
}

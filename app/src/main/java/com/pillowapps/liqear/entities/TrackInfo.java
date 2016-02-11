package com.pillowapps.liqear.entities;

public class TrackInfo {
    private String url;
    private long audioId;
    private long ownerId;

    public TrackInfo(String url) {
        this.url = url;
    }

    public TrackInfo(String url, long audioId, long ownerId) {
        this.url = url;
        this.audioId = audioId;
        this.ownerId = ownerId;
    }

    public String getUrl() {
        return url;
    }

    public long getAudioId() {
        return audioId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    @Override
    public String toString() {
        return "TrackInfo{" +
                "url='" + url + '\'' +
                ", audioId=" + audioId +
                ", ownerId=" + ownerId +
                '}';
    }
}

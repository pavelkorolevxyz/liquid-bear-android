package com.pillowapps.liqear.entities.vk;

import com.google.gson.annotations.SerializedName;

public class VkTrack {
    @SerializedName("id")
    private long audioId;
    @SerializedName("owner_id")
    private long ownerId;
    @SerializedName("artist")
    private String artist;
    @SerializedName("title")
    private String title;
    @SerializedName("url")
    private String url;

    public VkTrack() {
    }

    public long getAudioId() {
        return audioId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "VkTrack{" +
                "audioId='" + audioId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

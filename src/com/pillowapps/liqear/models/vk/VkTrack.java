package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

public class VkTrack {
    @SerializedName("id")
    private String audioId;
    @SerializedName("ownerId")
    private String ownerId;
    @SerializedName("artist")
    private String artist;
    @SerializedName("title")
    private String title;
    @SerializedName("url")
    private String url;

    public VkTrack() {
    }

    public String getAudioId() {
        return audioId;
    }

    public String getOwnerId() {
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

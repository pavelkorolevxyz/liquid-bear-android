package com.pillowapps.liqear.entities.vk;

import com.google.gson.annotations.SerializedName;

public class VkAlbum {
    @SerializedName("id")
    private long albumId;
    @SerializedName("owner_id")
    private long ownerId;
    @SerializedName("title")
    private String title;

    public VkAlbum() {
    }

    public long getAlbumId() {
        return albumId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }
}

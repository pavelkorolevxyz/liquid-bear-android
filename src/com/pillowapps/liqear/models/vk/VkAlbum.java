package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

public class VkAlbum {
    @SerializedName("album_id")
    private String albumId;
    @SerializedName("owner_id")
    private String ownerId;
    @SerializedName("title")
    private String title;

    public VkAlbum() {
    }

    public String getAlbumId() {
        return albumId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }
}

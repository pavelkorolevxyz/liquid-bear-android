package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VkAlbumsResponse {
    @SerializedName("items")
    private List<VkAlbum> albums;
    @SerializedName("count")
    private int count;

    public VkAlbumsResponse() {
    }

    public List<VkAlbum> getAlbums() {
        return albums;
    }

    public int getCount() {
        return count;
    }
}

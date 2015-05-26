package com.pillowapps.liqear.entities.vk.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.vk.VkLyrics;
import com.pillowapps.liqear.entities.vk.VkResponse;

import java.util.List;

public class VkLyricsResponseRoot extends VkResponse {
    @SerializedName("response")
    private List<VkLyrics> response;

    public VkLyricsResponseRoot() {
    }

    public List<VkLyrics> getLyricsList() {
        return response;
    }
}

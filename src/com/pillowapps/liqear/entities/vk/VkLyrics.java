package com.pillowapps.liqear.entities.vk;

import com.google.gson.annotations.SerializedName;

public class VkLyrics {
    @SerializedName("text")
    private String text;

    public VkLyrics() {
    }

    public String getText() {
        return text;
    }
}

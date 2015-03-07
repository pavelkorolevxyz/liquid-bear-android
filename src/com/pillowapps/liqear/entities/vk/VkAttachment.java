package com.pillowapps.liqear.entities.vk;

import com.google.gson.annotations.SerializedName;

public class VkAttachment {
    @SerializedName("type")
    private String type;
    @SerializedName("audio")
    private VkTrack audio;

    public VkAttachment() {
    }

    public String getType() {
        return type;
    }

    public VkTrack getAudio() {
        return audio;
    }

    public VkAttachmentType getAttachmentType() {
        if (audio != null) {
            return VkAttachmentType.AUDIO;
        } else {
            return VkAttachmentType.OTHER;
        }
    }
}

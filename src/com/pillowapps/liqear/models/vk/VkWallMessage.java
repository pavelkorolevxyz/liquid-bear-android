package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VkWallMessage {
    @SerializedName("attachments")
    private List<VkAttachment> attachments;

    public VkWallMessage() {
    }

    public List<VkAttachment> getAttachments() {
        return attachments;
    }
}

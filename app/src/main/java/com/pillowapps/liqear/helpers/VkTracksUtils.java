package com.pillowapps.liqear.helpers;

import com.pillowapps.liqear.entities.vk.VkAttachment;
import com.pillowapps.liqear.entities.vk.VkAttachmentType;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.entities.vk.VkWallMessage;

import java.util.ArrayList;
import java.util.List;

public class VkTracksUtils {

    private VkTracksUtils() {
        // no-op
    }

    public static List<VkTrack> getTracks(List<VkWallMessage> posts) {
        ArrayList<VkTrack> tracks = new ArrayList<>();
        for (VkWallMessage post : posts) {
            List<VkAttachment> attachments = post.getAttachments();
            if (attachments == null) continue;
            for (VkAttachment attachment : attachments) {
                if (attachment.getAttachmentType() == VkAttachmentType.AUDIO) {
                    tracks.add(attachment.getAudio());
                }
            }
        }
        return tracks;
    }
}

package com.pillowapps.liqear.models.vk;

import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.helpers.TrackUtils;
import com.pillowapps.liqear.helpers.VkCallbackUtils;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.network.service.VkApiService;

public class VkStatusModel {
    private VkApiService vkService = ServiceHelper.getVkService();

    public void updateStatus(Track track, final VkSimpleCallback<VkResponse> callback) {
        if (TrackUtils.vkInfoAvailable(track)) {
            String audio = track.getOwnerId() + " " + track.getAudioId();
            vkService.updateStatus(audio, VkCallbackUtils.getTransitiveCallback(callback));
        } else {
            vkService.setAudioStatusWithSearch(TrackUtils.getNotation(track), VkCallbackUtils.getTransitiveCallback(callback));
        }
    }
}


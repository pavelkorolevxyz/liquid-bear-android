package com.pillowapps.liqear.models.vk;

import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkLyrics;
import com.pillowapps.liqear.entities.vk.roots.VkLyricsResponseRoot;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.callbacks.VkCallback;
import com.pillowapps.liqear.network.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.network.service.VkApiService;

import java.util.List;

public class VkLyricsModel {
    private VkApiService vkService = ServiceHelper.getVkService();

    public void getLyrics(Track track, int index, VkSimpleCallback<VkLyrics> callback) {
        getLyrics(track.getNotation(), index, callback);
    }

    public void getLyrics(String notation, int index, final VkSimpleCallback<VkLyrics> callback) {
        vkService.getLyrics(notation, index, new VkCallback<VkLyricsResponseRoot>() {
            @Override
            public void success(VkLyricsResponseRoot data) {
                List<VkLyrics> lyricsList = data.getLyricsList();
                int lyricsListSize = lyricsList.size();
                if (lyricsListSize > 1) {
                    callback.success(lyricsList.get(1));
                } else if (lyricsListSize == 1) {
                    callback.success(lyricsList.get(0));
                } else {
                    callback.success(null);
                }
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }
}

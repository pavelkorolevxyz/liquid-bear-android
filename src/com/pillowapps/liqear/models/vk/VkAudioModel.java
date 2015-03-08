package com.pillowapps.liqear.models.vk;

import com.pillowapps.liqear.entities.vk.VkAlbum;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.entities.vk.roots.VkAlbumsResponseRoot;
import com.pillowapps.liqear.entities.vk.roots.VkTracksResponseRoot;
import com.pillowapps.liqear.helpers.StringUtils;
import com.pillowapps.liqear.helpers.VkCallbackUtils;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.callbacks.VkCallback;
import com.pillowapps.liqear.network.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.network.service.VkApiService;

import java.util.List;

public class VkAudioModel {
    private VkApiService vkService = ServiceHelper.getVkService();

    public void getVkUserAudio(long uid, int count, int offset, final VkSimpleCallback<List<VkTrack>> callback) {
        vkService.getAudio(uid, count, offset, new VkCallback<VkTracksResponseRoot>() {
            @Override
            public void success(VkTracksResponseRoot data) {
                callback.success(data.getResponse().getTracks());
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }

    public void getVkGroupAudio(long gid, int count, int offset, final VkSimpleCallback<List<VkTrack>> callback) {
        vkService.getGroupAudio(gid, count, offset, new VkCallback<VkTracksResponseRoot>() {
            @Override
            public void success(VkTracksResponseRoot data) {
                callback.success(data.getResponse().getTracks());
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }

    public void getUserVkAlbums(long ownerId, int offset, int count, final VkSimpleCallback<List<VkAlbum>> callback) {
        vkService.getAlbums(ownerId, offset, count, new VkCallback<VkAlbumsResponseRoot>() {
            @Override
            public void success(VkAlbumsResponseRoot data) {
                List<VkAlbum> albums = data.getResponse().getAlbums();
                callback.success(albums);
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }

    public void getGroupVkAlbums(long ownerId, int offset, int count, final VkSimpleCallback<List<VkAlbum>> callback) {
        vkService.getAlbums(-ownerId, offset, count, new VkCallback<VkAlbumsResponseRoot>() {
            @Override
            public void success(VkAlbumsResponseRoot data) {
                List<VkAlbum> albums = data.getResponse().getAlbums();
                callback.success(albums);
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }

    public void getUserAudioFromAlbum(long uid, long albumId, final int count, int offset,
                                      final VkSimpleCallback<List<VkTrack>> callback) {
        vkService.getAudio(String.valueOf(uid), String.valueOf(albumId), count, offset,
                new VkCallback<VkTracksResponseRoot>() {
                    @Override
                    public void success(VkTracksResponseRoot data) {
                        callback.success(data.getResponse().getTracks());
                    }

                    @Override
                    public void failure(VkError error) {
                        callback.failure(error);
                    }
                });
    }

    public void getGroupAudioFromAlbum(long gid, long albumId, final int count, int offset,
                                       final VkSimpleCallback<List<VkTrack>> callback) {
        vkService.getAudio(String.format("-%d", gid), String.valueOf(albumId), count, offset,
                new VkCallback<VkTracksResponseRoot>() {
                    @Override
                    public void success(VkTracksResponseRoot data) {
                        callback.success(data.getResponse().getTracks());
                    }

                    @Override
                    public void failure(VkError error) {
                        callback.failure(error);
                    }
                });
    }

    public void searchAudio(String query, int offset, int count, final VkSimpleCallback<List<VkTrack>> callback) {
        vkService.searchAudio(query.replaceAll("\\?", ""), offset, count,
                new VkCallback<VkTracksResponseRoot>() {
                    @Override
                    public void success(VkTracksResponseRoot data) {
                        callback.success(data.getResponse().getTracks());
                    }

                    @Override
                    public void failure(VkError error) {
                        callback.failure(error);
                    }
                });
    }

    public void addToUserAudio(long audioId, long ownerId, VkSimpleCallback<VkResponse> callback) {
        vkService.addAudio(audioId, ownerId, VkCallbackUtils.getTransitiveCallback(callback));
    }

    public void addToUserAudioFast(String notation, final VkSimpleCallback<VkResponse> callback) {
        vkService.addAudioFast(StringUtils.escapeString(notation), VkCallbackUtils.getTransitiveCallback(callback));
    }

    public void getVkRecommendations(int count, int offset, final VkSimpleCallback<List<VkTrack>> callback) {
        vkService.getRecommendations(offset, count, new VkCallback<VkTracksResponseRoot>() {
            @Override
            public void success(VkTracksResponseRoot data) {
                callback.success(data.getResponse().getTracks());
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }
}

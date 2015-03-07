package com.pillowapps.liqear.network;

import com.pillowapps.liqear.helpers.StringUtils;
import com.pillowapps.liqear.helpers.VkTracksUtils;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkAlbum;
import com.pillowapps.liqear.entities.vk.roots.VkAlbumsResponseRoot;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.roots.VkGetUsersResponseRoot;
import com.pillowapps.liqear.entities.vk.VkGroup;
import com.pillowapps.liqear.entities.vk.roots.VkGroupsResponseRoot;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.entities.vk.roots.VkTracksResponseRoot;
import com.pillowapps.liqear.entities.vk.VkUser;
import com.pillowapps.liqear.entities.vk.roots.VkUsersResponseRoot;
import com.pillowapps.liqear.entities.vk.VkWallMessage;
import com.pillowapps.liqear.entities.vk.roots.VkWallMessagesResponseRoot;
import com.pillowapps.liqear.network.callbacks.VkCallback;
import com.pillowapps.liqear.network.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.network.service.VkApiService;

import java.util.List;

public class VkRequestManager {

    public static VkRequestManager instance;
    private VkApiService vkService = ServiceHelper.getVkService();

    private VkRequestManager() {

    }

    private VkCallback<VkResponse> getTransitiveCallback(final VkSimpleCallback<VkResponse> callback) {
        return new VkCallback<VkResponse>() {
            @Override
            public void success(VkResponse data) {
                callback.success(data);
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        };
    }

    public static VkRequestManager getInstance() {
        if (instance == null) {
            instance = new VkRequestManager();
        }
        return instance;
    }

    public void getUserInfoVk(long userId, final VkSimpleCallback<VkUser> callback) {
        String fields = "first_name,last_name,photo_medium";
        vkService.getUser(userId, fields, new VkCallback<VkGetUsersResponseRoot>() {
            @Override
            public void success(VkGetUsersResponseRoot data) {
                List<VkUser> users = data.getUsers();
                if (users == null || users.size() == 0) return;
                VkUser user = users.get(0);
                callback.success(user);
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }

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

    public void getVkUserFavoritesAudio(int count, int offset, final VkSimpleCallback<List<VkTrack>> callback) {
        vkService.getFavoriteWallMessages(offset, count, new VkCallback<VkWallMessagesResponseRoot>() {
            @Override
            public void success(VkWallMessagesResponseRoot data) {
                List<VkWallMessage> posts = data.getResponse().getPosts();
                List<VkTrack> tracks = VkTracksUtils.getTracks(posts);
                callback.success(tracks);
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }

    public void getVkNewsFeedTracks(int count, int offset, final VkSimpleCallback<List<VkTrack>> callback) {
        vkService.getNewsfeedWallMessages(offset, count, new VkCallback<VkWallMessagesResponseRoot>() {
            @Override
            public void success(VkWallMessagesResponseRoot data) {
                List<VkWallMessage> posts = data.getResponse().getPosts();
                List<VkTrack> tracks = VkTracksUtils.getTracks(posts);
                callback.success(tracks);
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

    public void getVkUserWallAudio(long ownerId, int count, int offset, final VkSimpleCallback<List<VkTrack>> callback) {
        vkService.getWallMessages(ownerId, offset, count, new VkCallback<VkWallMessagesResponseRoot>() {
            @Override
            public void success(VkWallMessagesResponseRoot data) {
                List<VkWallMessage> posts = data.getResponse().getPosts();
                List<VkTrack> tracks = VkTracksUtils.getTracks(posts);
                callback.success(tracks);
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }

    public void getVkGroupWallAudio(long ownerId, int count, int offset, final VkSimpleCallback<List<VkTrack>> callback) {
        vkService.getWallMessages(-ownerId, offset, count, new VkCallback<VkWallMessagesResponseRoot>() {

            @Override
            public void success(VkWallMessagesResponseRoot data) {
                List<VkWallMessage> posts = data.getResponse().getPosts();
                List<VkTrack> tracks = VkTracksUtils.getTracks(posts);
                callback.success(tracks);
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }

    public void updateStatus(Track track, final VkSimpleCallback<VkResponse> callback) {
        if (track.hasAudioString()) {
            String audio = track.getOwnerId() + " " + track.getAid();
            vkService.updateStatus(audio, getTransitiveCallback(callback));
        } else {
            vkService.setAudioStatusWithSearch(track.getNotation(), getTransitiveCallback(callback));
        }
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
        vkService.addAudio(audioId, ownerId, getTransitiveCallback(callback));
    }

    public void addToUserAudioFast(String notation, final VkSimpleCallback<VkResponse> callback) {
        vkService.addAudioFast(StringUtils.escapeString(notation), getTransitiveCallback(callback));
    }

    public void getGroups(int offset, int count, final VkSimpleCallback<List<VkGroup>> callback) {
        vkService.getGroups(1, offset, count, new VkCallback<VkGroupsResponseRoot>() {
            @Override
            public void success(VkGroupsResponseRoot data) {
                callback.success(data.getResponse().getGroups());
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
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

    public void getFriends(int count, int offset, final VkSimpleCallback<List<VkUser>> callback) {
        String fields = "first_name,last_name,uid,photo_medium";
        String order = "hints";
        vkService.getFriends(fields, order, offset, count, new VkCallback<VkUsersResponseRoot>() {
            @Override
            public void success(VkUsersResponseRoot data) {
                callback.success(data.getResponse().getUsers());
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }
}

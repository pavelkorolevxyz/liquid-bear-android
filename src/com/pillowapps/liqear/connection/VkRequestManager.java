package com.pillowapps.liqear.connection;

import com.pillowapps.liqear.helpers.StringUtils;
import com.pillowapps.liqear.models.Track;
import com.pillowapps.liqear.models.vk.VkAlbum;
import com.pillowapps.liqear.models.vk.VkAlbumsResponseRoot;
import com.pillowapps.liqear.models.vk.VkGroup;
import com.pillowapps.liqear.models.vk.VkGroupsResponseRoot;
import com.pillowapps.liqear.models.vk.VkTrack;
import com.pillowapps.liqear.models.vk.VkTracksResponseRoot;
import com.pillowapps.liqear.models.vk.VkUser;
import com.pillowapps.liqear.models.vk.VkWallMessage;
import com.pillowapps.liqear.models.vk.VkWallMessagesResponseRoot;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VkRequestManager {

    public static VkRequestManager instance;
    private VkApiService vkService = ServiceHelper.getVkService();

    private VkRequestManager() {

    }

    public static VkRequestManager getInstance() {
        if (instance == null) {
            instance = new VkRequestManager();
        }
        return instance;
    }

    public void getUserInfoVk(long userId, final Callback<VkUser> callback) {
        String fields = "first_name,last_name,photo_medium";
        vkService.getUser(userId, fields, new Callback<List<VkUser>>() {
            @Override
            public void success(List<VkUser> users, Response response) {
                if (users.size() == 0) return;
                VkUser user = users.get(0);
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getVkUserAudio(long uid, int count, int offset, final Callback<List<VkTrack>> callback) {
        vkService.getAudio(uid, count, offset, new Callback<VkTracksResponseRoot>() {
            @Override
            public void success(VkTracksResponseRoot root, Response response) {
                callback.success(root.getResponse().getTracks(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getVkGroupAudio(long gid, int count, int offset, final Callback<List<VkTrack>> callback) {
        vkService.getGroupAudio(gid, count, offset, new Callback<VkTracksResponseRoot>() {
            @Override
            public void success(VkTracksResponseRoot root, Response response) {
                callback.success(root.getResponse().getTracks(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getVkUserFavoritesAudio(int count, int offset, final Callback<List<VkTrack>> callback) {
        vkService.getFavoriteWallMessages(offset, count, new Callback<VkWallMessagesResponseRoot>() {
            @Override
            public void success(VkWallMessagesResponseRoot root, Response response) {
                List<VkWallMessage> posts = root.getResponse().getPosts();
                List<VkTrack> tracks = new ArrayList<>();
                //todo get tracks from posts
                callback.success(tracks, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getVkNewsFeedTracks(int count, int offset, final Callback<List<VkTrack>> callback) {
        vkService.getNewsfeedWallMessages(offset, count, new Callback<VkWallMessagesResponseRoot>() {
            @Override
            public void success(VkWallMessagesResponseRoot root, Response response) {
                List<VkWallMessage> posts = root.getResponse().getPosts();
                List<VkTrack> tracks = new ArrayList<>();
                //todo get tracks from posts
                callback.success(tracks, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getUserVkAlbums(long ownerId, int offset, int count, final Callback<List<VkAlbum>> callback) {
        vkService.getAlbums(ownerId, offset, count, new Callback<VkAlbumsResponseRoot>() {
            @Override
            public void success(VkAlbumsResponseRoot root, Response response) {
                List<VkAlbum> albums = root.getResponse().getAlbums();
                //todo get tracks from albums
                callback.success(albums, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getGroupVkAlbums(long ownerId, int offset, int count, final Callback<List<VkAlbum>> callback) {
        vkService.getAlbums(-ownerId, offset, count, new Callback<VkAlbumsResponseRoot>() {
            @Override
            public void success(VkAlbumsResponseRoot root, Response response) {
                List<VkAlbum> albums = root.getResponse().getAlbums();
                //todo get tracks from albums
                callback.success(albums, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getVkUserWallAudio(long ownerId, int count, int offset, final Callback<List<VkTrack>> callback) {
        vkService.getWallMessages(ownerId, offset, count, new Callback<VkWallMessagesResponseRoot>() {
            @Override
            public void success(VkWallMessagesResponseRoot root, Response response) {
                List<VkWallMessage> posts = root.getResponse().getPosts();
                List<VkTrack> tracks = new ArrayList<>();
                //todo get tracks from posts
                callback.success(tracks, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getVkGroupWallAudio(long ownerId, int count, int offset, final Callback<List<VkTrack>> callback) {
        vkService.getWallMessages(-ownerId, offset, count, new Callback<VkWallMessagesResponseRoot>() {
            @Override
            public void success(VkWallMessagesResponseRoot root, Response response) {
                List<VkWallMessage> posts = root.getResponse().getPosts();
                List<VkTrack> tracks = new ArrayList<>();
                //todo get tracks from posts
                callback.success(tracks, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void updateStatus(Track track, Callback<Object> callback) {
        if (track.hasAudioString()) {
            String audio = track.getOwnerId() + " " + track.getAid();
            vkService.updateStatus(audio, callback);
        } else {
            vkService.setAudioStatusWithSearch(track.getNotation(), callback);
        }
    }

    public void getUserAudioFromAlbum(long uid, long albumId, final int count, int offset,
                                      final Callback<List<VkTrack>> callback) {
        vkService.getAudio(String.valueOf(uid), String.valueOf(albumId), count, offset,
                new Callback<VkTracksResponseRoot>() {
                    @Override
                    public void success(VkTracksResponseRoot root, Response response) {
                        callback.success(root.getResponse().getTracks(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                });
    }

    public void getGroupAudioFromAlbum(long gid, long albumId, final int count, int offset,
                                       final Callback<List<VkTrack>> callback) {
        vkService.getAudio(String.format("-%d", gid), String.valueOf(albumId), count, offset,
                new Callback<VkTracksResponseRoot>() {
                    @Override
                    public void success(VkTracksResponseRoot root, Response response) {
                        callback.success(root.getResponse().getTracks(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                });
    }

    public void searchAudio(String query, int offset, int count, final Callback<List<VkTrack>> callback) {
        vkService.searchAudio(query.replaceAll("\\?", ""), offset, count,
                new Callback<VkTracksResponseRoot>() {
                    @Override
                    public void success(VkTracksResponseRoot root, Response response) {
                        callback.success(root.getResponse().getTracks(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                });
    }

    public void addToUserAudio(long audioId, long ownerId, Callback<Object> callback) {
        vkService.addAudio(audioId, ownerId, callback);
    }

    public void addToUserAudioFast(String notation, Callback<Object> callback) {
        vkService.addAudioFast(StringUtils.escapeString(notation), callback);
    }

    public void getGroups(int offset, int count, final Callback<List<VkGroup>> callback) {
        vkService.getGroups(1, offset, count, new Callback<VkGroupsResponseRoot>() {
            @Override
            public void success(VkGroupsResponseRoot root, Response response) {
                callback.success(root.getResponse().getGroups(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }
}

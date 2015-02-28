package com.pillowapps.liqear.connection;

import com.pillowapps.liqear.models.vk.VkAlbum;
import com.pillowapps.liqear.models.vk.VkAlbumsResponseRoot;
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

    public void getUserInfoVk(String userId, final Callback<VkUser> callback) {
        String fields = "first_name,last_name,photo_medium";
        vkService.getUsers(userId, fields, new Callback<List<VkUser>>() {
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

    public void getVkUserAudio(String uid, int count, int offset, final Callback<List<VkTrack>> callback) {
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

    public void getVkGroupAudio(String gid, int count, int offset, final Callback<List<VkTrack>> callback) {
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

    public void getUserVkAlbums(String ownerId, int offset, int count, final Callback<List<VkAlbum>> callback) {
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

    public void getGroupVkAlbums(String ownerId, int offset, int count, final Callback<List<VkAlbum>> callback) {
        vkService.getAlbums(String.format("-%s", ownerId), offset, count, new Callback<VkAlbumsResponseRoot>() {
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

    public void getVkUserWallAudio(String ownerId, int count, int offset, final Callback<List<VkTrack>> callback) {
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

    public void getVkGroupWallAudio(String ownerId, int count, int offset, final Callback<List<VkTrack>> callback) {
        vkService.getWallMessages(String.format("-%s", ownerId), offset, count, new Callback<VkWallMessagesResponseRoot>() {
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
}

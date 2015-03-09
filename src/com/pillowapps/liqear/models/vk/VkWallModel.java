package com.pillowapps.liqear.models.vk;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.entities.vk.VkWallMessage;
import com.pillowapps.liqear.entities.vk.roots.VkWallMessagesResponseRoot;
import com.pillowapps.liqear.helpers.VkTracksUtils;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.callbacks.VkCallback;
import com.pillowapps.liqear.network.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.network.service.VkApiService;

import java.io.File;
import java.util.List;

public class VkWallModel {
    private VkApiService vkService = ServiceHelper.getVkService();

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

    public void postMessage(String message, String imageUrl, final Track track) {
        StringBuilder attachmentsBuilder = new StringBuilder();

        if (imageUrl == null) {
            uploadWallImage(imageUrl);
            long photoId = 0;
            attachmentsBuilder.append(photoId);
            if (track.getOwnerId() != 0) {
                if (photoId != 0) {
                    attachmentsBuilder.append(",");
                }
                attachmentsBuilder.append("audio")
                        .append(track.getOwnerId())
                        .append("_")
                        .append(track.getAid());
            }
        } else {
            if (track.getOwnerId() != 0) {
                attachmentsBuilder.append("audio")
                        .append(track.getOwnerId())
                        .append("_")
                        .append(track.getAid());
            }
        }
    }

    private void uploadWallImage(String imageUrl) {
        final File cachedImage = ImageLoader.getInstance().getDiscCache().get(imageUrl);
        String server = getPhotosWallUploadServer();
//        uploadUserPhoto(cachedImage, (String) result.getObject(), callback);
    }

    private String getPhotosWallUploadServer() {
        return null;
    }
}

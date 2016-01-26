package com.pillowapps.liqear.models.vk;

import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.callbacks.retrofit.VkCallback;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkPhotoUploadResult;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.entities.vk.VkSavePhotoItem;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.entities.vk.VkWallMessage;
import com.pillowapps.liqear.entities.vk.roots.VkSavePhotoRoot;
import com.pillowapps.liqear.entities.vk.roots.VkUploadServerRoot;
import com.pillowapps.liqear.entities.vk.roots.VkWallMessagesResponseRoot;
import com.pillowapps.liqear.helpers.VkTracksUtils;
import com.pillowapps.liqear.network.service.VkApiService;

import java.net.URL;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VkWallModel {
    private VkApiService vkService;

    public VkWallModel(VkApiService api) {
        this.vkService = api;
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

    public void postMessage(final String message, final String imageUrl, final Track track,
                            final VkSimpleCallback<VkResponse> callback) {

        Subscriber<VkResponse> subscriber = new Subscriber<VkResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                VkError error = new VkError();
                error.setErrorMessage("Failed to post wall message");
                callback.failure(error);
            }

            @Override
            public void onNext(VkResponse vkResponse) {
                callback.success(vkResponse);
            }
        };

        final StringBuilder attachmentsBuilder = new StringBuilder();
        if (imageUrl == null) { // If there is no image to upload, add only audio
            if (track.getOwnerId() != 0) {
                attachmentsBuilder.append("audio")
                        .append(track.getOwnerId())
                        .append("_")
                        .append(track.getAudioId());
            }
            postWall(message, attachmentsBuilder.toString()).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        } else {
            Observable<VkUploadServerRoot> uploadServerRootObservable = getPhotoUploadServer();
            uploadServerRootObservable.flatMap(vkUploadServerRoot -> {
                URL uploadUrl = vkUploadServerRoot.getUploadServer().getUploadUrl();
                return uploadWallImage(imageUrl, uploadUrl);
            }).flatMap(this::saveWallPhoto).flatMap(vkSavePhotoRoot -> {
                List<VkSavePhotoItem> photoItems = vkSavePhotoRoot.getPhotoItems();
                long photoId = 0;
                if (photoItems.size() > 0) {
                    VkSavePhotoItem photoItem = photoItems.get(0);
                    photoId = photoItem.getId();
                    attachmentsBuilder.append("photo").append(photoItem.getOwnerId())
                            .append("_")
                            .append(photoId);
                }
                if (track.getOwnerId() != 0) {
                    if (photoId != 0) {
                        attachmentsBuilder.append(",");
                    }
                    attachmentsBuilder.append("audio")
                            .append(track.getOwnerId())
                            .append("_")
                            .append(track.getAudioId());
                }
                return postWall(message, attachmentsBuilder.toString());
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        }
    }

    private Observable<VkResponse> postWall(String message, String attachments) {
        return vkService.postWallMessages(message, attachments);
    }

    private Observable<VkPhotoUploadResult> uploadWallImage(String imageUrl, URL uploadUrl) {

//        final File imageFile = ImageLoader.getInstance().getDiscCache().get(imageUrl);
//        CustomTypedFile typedFile = new CustomTypedFile(FileUtils.getMimeType(imageUrl),
//                imageFile, "photo.jpg");
//        Map<String, String> params = StringUtils.parseUrlParams(uploadUrl);
//        String endpoint = String.format("%s://%s", uploadUrl.getProtocol(), uploadUrl.getHost());
//        return ServiceHelper.getVkUploadService(endpoint).uploadPhoto(typedFile, params);
        return Observable.empty(); // todo change from UIL to Glide cache
    }

    private Observable<VkSavePhotoRoot> saveWallPhoto(VkPhotoUploadResult result) {
        return vkService.saveWallPhoto(result.getServer(), result.getPhoto(), result.getHash());
    }

    public Observable<VkUploadServerRoot> getPhotoUploadServer() {
        return vkService.getPhotoWallUploadServer();
    }
}

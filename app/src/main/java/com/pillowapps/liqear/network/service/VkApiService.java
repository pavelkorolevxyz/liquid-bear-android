package com.pillowapps.liqear.network.service;

import com.pillowapps.liqear.callbacks.retrofit.VkCallback;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.entities.vk.roots.VkAlbumsResponseRoot;
import com.pillowapps.liqear.entities.vk.roots.VkGetUsersResponseRoot;
import com.pillowapps.liqear.entities.vk.roots.VkGroupsResponseRoot;
import com.pillowapps.liqear.entities.vk.roots.VkLyricsResponseRoot;
import com.pillowapps.liqear.entities.vk.roots.VkSavePhotoRoot;
import com.pillowapps.liqear.entities.vk.roots.VkTrackUrlResponseRoot;
import com.pillowapps.liqear.entities.vk.roots.VkTracksResponseRoot;
import com.pillowapps.liqear.entities.vk.roots.VkUploadServerRoot;
import com.pillowapps.liqear.entities.vk.roots.VkUsersResponseRoot;
import com.pillowapps.liqear.entities.vk.roots.VkWallMessagesResponseRoot;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface VkApiService {

    @GET("/wall.get")
    void getWallMessages(@Query("owner_id") long ownerId,
                         @Query("offset") int offset,
                         @Query("count") int count,
                         Callback<VkWallMessagesResponseRoot> callback);

    @GET("/fave.getPosts")
    void getFavoriteWallMessages(@Query("offset") int offset,
                                 @Query("count") int count,
                                 Callback<VkWallMessagesResponseRoot> callback);

    @GET("/newsfeed.get")
    void getNewsfeedWallMessages(@Query("start_from") int offset,
                                 @Query("count") int count,
                                 Callback<VkWallMessagesResponseRoot> callback);

    @POST("/wall.post")
    Observable<VkResponse> postWallMessages(@Query("message") String message,
                                            @Query("attachment") String attachment);

    @GET("/audio.get")
    void getAudio(@Query("owner_id") long ownerId,
                  @Query("count") int count,
                  @Query("offset") int offset,
                  Callback<VkTracksResponseRoot> callback);

    @GET("/audio.get")
    void getGroupAudio(@Query("gid") long groupId,
                       @Query("count") int count,
                       @Query("offset") int offset,
                       Callback<VkTracksResponseRoot> callback);

    @GET("/audio.get")
    void getAudio(@Query("owner_id") long ownerId,
                  @Query("album_id") long albumId,
                  @Query("count") int count,
                  @Query("offset") int offset,
                  Callback<VkTracksResponseRoot> callback);

    @GET("/audio.search")
    void searchAudio(@Query("q") String q,
                     @Query("offset") int offset,
                     @Query("count") int count,
                     Callback<VkTracksResponseRoot> callback);

    @GET("/users.get")
    void getUser(@Query("user_ids") long uid,
                 @Query("fields") String fields,
                 VkCallback<VkGetUsersResponseRoot> callback);

    @POST("/status.set")
    void setAudioStatus(@Query("audio") String audio,
                        Callback<VkResponse> callback);

    @POST("/execute.searchAndPostStatus")
    void setAudioStatusWithSearch(@Query("q") String searchQuery,
                                  Callback<VkResponse> callback);

    @POST("/execute.ta")
    void addAudioWithSearch(@Query("q") String searchQuery,
                            Callback<VkResponse> callback);

    @GET("/groups.get")
    void getGroups(@Query("extended") int extended,
                   @Query("offset") int offset,
                   @Query("count") int count,
                   Callback<VkGroupsResponseRoot> callback);

    @GET("/audio.getAlbums")
    void getAlbums(@Query("owner_id") long ownerId,
                   @Query("offset") int offset,
                   @Query("count") int count,
                   Callback<VkAlbumsResponseRoot> callback);

    @GET("/audio.getRecommendations")
    void getRecommendations(@Query("offset") int offset,
                            @Query("count") int count,
                            Callback<VkTracksResponseRoot> callback);

    @GET("/friends.get")
    void getFriends(@Query("fields") String fields,
                    @Query("order") String order,
                    @Query("offset") int offset,
                    @Query("count") int count,
                    VkCallback<VkUsersResponseRoot> callback);

    @GET("/execute.getUrl")
    void getTrackUrl(@Query("notation") String trackNotation,
                     @Query("index") int index,
                     VkCallback<VkTrackUrlResponseRoot> callback);

    @GET("/execute.getUrlById")
    void getTrackUrlById(@Query("audioId") long audioId,
                         @Query("ownerId") long ownerId,
                         VkCallback<VkTrackUrlResponseRoot> callback);

    @GET("/execute.getLyrics")
    void getLyrics(@Query("notation") String trackNotation,
                   @Query("index") int index,
                   Callback<VkLyricsResponseRoot> callback);

    @POST("/photos.saveWallPhoto")
    Observable<VkSavePhotoRoot> saveWallPhoto(@Query("server") String server,
                                              @Query("photo") String photo,
                                              @Query("hash") String hash);

    @GET("/photos.getWallUploadServer")
    Observable<VkUploadServerRoot> getPhotoWallUploadServer();

    @POST("/status.set")
    void updateStatus(@Query("audio") String audioString,
                      Callback<VkResponse> callback);

    @POST("/audio.add")
    void addAudio(@Query("audio_id") long audioId,
                  @Query("owner_id") long ownerId,
                  Callback<VkResponse> callback);

    @POST("/execute.ta")
    void addAudioFast(@Query("q") String q,
                      Callback<VkResponse> callback);
}

package com.pillowapps.liqear.models.vk;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.VkAudioSearchActivity;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.callbacks.retrofit.VkCallback;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkAlbum;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.entities.vk.roots.VkAlbumsResponseRoot;
import com.pillowapps.liqear.entities.vk.roots.VkTrackUrlResponseRoot;
import com.pillowapps.liqear.entities.vk.roots.VkTracksResponseRoot;
import com.pillowapps.liqear.helpers.PreferencesScreenManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.StringUtils;
import com.pillowapps.liqear.helpers.TrackUtils;
import com.pillowapps.liqear.helpers.VkCallbackUtils;
import com.pillowapps.liqear.network.service.VkApiService;

import java.util.List;

public class VkAudioModel {
    private VkApiService vkService;
    private PreferencesScreenManager preferencesManager;

    public VkAudioModel(VkApiService api, PreferencesScreenManager preferencesManager) {
        this.vkService = api;
        this.preferencesManager = preferencesManager;
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
        vkService.getAudio(uid, albumId, count, offset,
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
        vkService.getAudio(-gid, albumId, count, offset,
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

    public void addToVk(final Context context, Track track) {
        if (preferencesManager.isVkAddSlow()) {
            if (track == null) return;
            Intent intent = new Intent(context, VkAudioSearchActivity.class);
            intent.putExtra(Constants.TARGET, TrackUtils.getNotation(track));
            context.startActivity(intent);
        } else {
            addToUserAudioFast(TrackUtils.getNotation(track), new VkSimpleCallback<VkResponse>() {
                @Override
                public void success(VkResponse data) {
                    Toast.makeText(context, R.string.added, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(VkError error) {

                }
            });
        }
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

    public void getTrackByNotation(Track track, int index, final VkSimpleCallback<VkTrack> callback) {
        vkService.getTrackUrl(TrackUtils.getNotation(track), index, new VkCallback<VkTrackUrlResponseRoot>() {
            @Override
            public void success(VkTrackUrlResponseRoot data) {
                List<VkTrack> tracks = data.getResponse();
                if (tracks.size() == 0) {
                    callback.success(null);
                } else if (tracks.get(1) != null) {
                    callback.success(tracks.get(1));
                } else if (tracks.get(0) != null) {
                    callback.success(tracks.get(0));
                }
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }

    public void getTrackById(Track track, int index, final VkSimpleCallback<VkTrack> callback) {
        vkService.getTrackUrlById(TrackUtils.getNotation(track), index, track.getAudioId(), track.getOwnerId(),
                new VkCallback<VkTrackUrlResponseRoot>() {
                    @Override
                    public void success(VkTrackUrlResponseRoot data) {
                        List<VkTrack> tracks = data.getResponse();
                        if (tracks.size() == 0) {
                            callback.success(null);
                        } else if (tracks.get(0) != null) {
                            callback.success(tracks.get(0));
                        } else if (tracks.get(2) != null) {
                            callback.success(tracks.get(2));
                        } else if (tracks.get(1) != null) {
                            callback.success(tracks.get(1));
                        }
                    }

                    @Override
                    public void failure(VkError error) {
                        callback.failure(error);
                    }
                });
    }

    public void getTrack(Track trackToFind, int index, VkSimpleCallback<VkTrack> callback) {
        if (TrackUtils.vkInfoAvailable(trackToFind)) {
            getTrackById(trackToFind, index, callback);
        } else {
            getTrackByNotation(trackToFind, index, callback);
        }
    }
}

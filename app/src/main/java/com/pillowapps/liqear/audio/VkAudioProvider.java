package com.pillowapps.liqear.audio;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.TrackInfo;
import com.pillowapps.liqear.entities.exception.NoNetworkConnectionException;
import com.pillowapps.liqear.entities.exception.VkException;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.helpers.NetworkManager;
import com.pillowapps.liqear.models.vk.VkAudioModel;

import javax.inject.Inject;

import rx.Observable;

public class VkAudioProvider {

    private VkAudioModel vkAudioModel;
    private NetworkManager networkManager;

    @Inject
    public VkAudioProvider(VkAudioModel vkAudioModel, NetworkManager networkManager) {
        this.vkAudioModel = vkAudioModel;
        this.networkManager = networkManager;
    }

    public Observable<TrackInfo> getTrackInfo(@NonNull final Track track) {
        final String url = track.getUrl();
        if (url == null || url.isEmpty()) {
            return Observable.create(subscriber -> {
                if (!networkManager.isOnline()) {
                    subscriber.onError(new NoNetworkConnectionException());
                    return;
                }

                VkSimpleCallback<VkTrack> callback = new VkSimpleCallback<VkTrack>() {
                    @Override
                    public void success(VkTrack vkTrack) {
                        TrackInfo trackInfo = new TrackInfo(vkTrack.getUrl(), vkTrack.getAudioId(), vkTrack.getOwnerId());
                        subscriber.onNext(trackInfo);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void failure(VkError error) {
                        subscriber.onError(new VkException(error.getErrorMessage()));
                    }
                };
                vkAudioModel.getTrack(track, 0, callback);
            });
        } else {
            TrackInfo trackInfo = new TrackInfo(url);
            return Observable.just(trackInfo);
        }
    }

}

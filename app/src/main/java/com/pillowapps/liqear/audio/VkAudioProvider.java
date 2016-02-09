package com.pillowapps.liqear.audio;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.helpers.NetworkUtils;
import com.pillowapps.liqear.models.vk.VkAudioModel;

import javax.inject.Inject;

import rx.Observable;

public class VkAudioProvider {

    private VkAudioModel vkAudioModel;

    @Inject
    public VkAudioProvider(VkAudioModel vkAudioModel) {
        this.vkAudioModel = vkAudioModel;
    }

    public Observable<String> getAudioUrl(@NonNull final Track track) {
        final String url = track.getUrl();
        if (url == null || url.isEmpty()) {
            return Observable.create(subscriber -> {
                if (!NetworkUtils.isOnline()) {
                    subscriber.onError(new NoNetworkConnectionException());
                    return;
                }

                VkSimpleCallback<VkTrack> callback = new VkSimpleCallback<VkTrack>() {
                    @Override
                    public void success(VkTrack track) {
                        subscriber.onNext(track.getUrl());
                        subscriber.onCompleted();
                    }

                    @Override
                    public void failure(VkError error) {
                        subscriber.onError(new VkException());
                    }
                };
                vkAudioModel.getTrack(track, 0, callback);
            });
        } else {
            return Observable.just(url);
        }
    }

}

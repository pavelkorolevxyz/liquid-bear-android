package com.pillowapps.liqear.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pillowapps.liqear.callbacks.PassiveCallback;
import com.pillowapps.liqear.callbacks.VkPassiveCallback;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.PreferencesScreenManager;
import com.pillowapps.liqear.helpers.TimeUtils;
import com.pillowapps.liqear.models.lastfm.LastfmTrackModel;
import com.pillowapps.liqear.models.vk.VkStatusModel;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class TickModel {

    private int seconds;

    private LastfmTrackModel lastfmTrackModel;
    private VkStatusModel vkStatusModel;

    private CompositeSubscription nowplayingSubscription = new CompositeSubscription();
    private CompositeSubscription scrobblingSubscription = new CompositeSubscription();
    private boolean scrobbled = false;
    private Context context;
    private PreferencesScreenManager preferencesManager;

    @Inject
    public TickModel(Context context, LastfmTrackModel lastfmTrackModel, VkStatusModel vkStatusModel, PreferencesScreenManager preferencesManager) {
        this.context = context;
        this.lastfmTrackModel = lastfmTrackModel;
        this.vkStatusModel = vkStatusModel;
        this.preferencesManager = preferencesManager;
    }

    public void startNowplayingUpdater(@NonNull Track track) {
        stopNowplayingUpdater();
        nowplayingSubscription.add(
                Observable.interval(15, TimeUnit.SECONDS)
                        .startWith(-1L)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                            Timber.d("Nowplaying send");
                            updateNowPlaying(track);
                        },throwable -> {
                            Timber.e(throwable, "Nowplaying error");
                        })
        );
    }

    public void stopNowplayingUpdater() {
        nowplayingSubscription.clear();
    }

    public Observable<Long> getPlayProgressUpdater() {
        return Observable.interval(50, TimeUnit.MILLISECONDS)
                .startWith(-1L);
    }

    //todo add lastfm auth check
    public void startScrobblerUpdater(@NonNull Track track) {
        stopScrobblerUpdater();
        if (scrobbled) {
            return;
        }
        scrobblingSubscription.add(
                Observable.interval(1, TimeUnit.SECONDS)
                        .doOnCompleted(() -> seconds = 0)
                        .subscribe(aLong -> {
                            seconds++;
                            int playedPercent = (int) (((float) seconds / (track.getDuration() / 1000f)) * 100);
                            Timber.d("Scrobbling " + seconds + " / " + (track.getDuration() / 1000f) + " = " + playedPercent + "%");
                            if (playedPercent >= preferencesManager.getPercentsToScrobble()) {
                                Timber.d("Send scrobbling");
                                scrobble(track);
                            }
                        }, throwable -> {
                            Timber.e(throwable, "Scrobbling error");
                        })
        );
    }

    public void stopScrobblerUpdater() {
        scrobblingSubscription.clear();
    }

    private void scrobble(@NonNull Track track) {
        scrobblingSubscription.clear();
        scrobbled = true;
        lastfmTrackModel.scrobble(track.getArtist(), track.getTitle(), track.getAlbum(), TimeUtils.getCurrentTimeInSeconds(), new PassiveCallback()); // todo not passive callback but saving in database
    }

    public void clearScrobbling() {
        scrobbled = false;
        seconds = 0;
    }

    //todo add lastfm auth check
    private void updateNowPlaying(final Track currentTrack) {
        if (preferencesManager.isNowplayingLastfmEnabled()) {
            lastfmTrackModel.nowplaying(currentTrack, new PassiveCallback());
        }
        if (preferencesManager.isNowplayingVkEnabled()) {
            vkStatusModel.updateStatus(currentTrack, new VkPassiveCallback());
        }
    }

    public Observable<Long> getTimer(int minutes) {
        return Observable.timer(minutes, TimeUnit.MINUTES);
    }

    public void close() {
        nowplayingSubscription.clear();
        scrobblingSubscription.clear();
    }
}

package com.pillowapps.liqear.audio;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.android.exoplayer.ExoPlayer;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.audio.player.PreparedMediaPlayer;
import com.pillowapps.liqear.audio.player.RxMediaPlayer;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.events.NetworkStateChangeEvent;
import com.pillowapps.liqear.entities.events.PauseEvent;
import com.pillowapps.liqear.entities.events.PlayEvent;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.helpers.NetworkUtils;
import com.pillowapps.liqear.models.vk.VkAudioModel;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MusicService extends Service {

    private static final int LIQUID_BEAR_ID = 1314;

    public static final String ACTION_PLAY_PAUSE = "com.pillowapps.liqear.TOGGLE_PLAYBACK";
    public static final String ACTION_PREV = "com.pillowapps.liqear.PREV";
    public static final String ACTION_NEXT = "com.pillowapps.liqear.NEXT";
    public static final String ACTION_CLOSE = "com.pillowapps.liqear.FORCE_CLOSE";
    public static final String ACTION_SHUFFLE = "com.pillowapps.liqear.SHUFFLE";
    public static final String ACTION_REPEAT = "com.pillowapps.liqear.REPEAT";
    public static final String ACTION_LOVE = "com.pillowapps.liqear.LOVE";
    public static final String ACTION_ADD_TO_VK = "com.pillowapps.liqear.ADD_TO_VK";
    public static final String ACTION_ADD_TO_VK_FAST = "com.pillowapps.liqear.ADD_TO_VK_FAST";
    public static final String CHANGE_SHAKE_PREFERENCE = "com.pillowapps.liqear.CHANGE_SHAKE_PREFERENCE";
    public static final String ACTION_PLAY = "com.pillowapps.liqear.PLAY";
    public static final String ACTION_PAUSE = "com.pillowapps.liqear.PAUSE";

    private LocalBinder binder = new LocalBinder();
    private ExoPlayer mediaPlayer;
    private RxMediaPlayer rxMediaPlayer;

    @Inject
    Timeline timeline;

    @Inject
    VkAudioModel vkAudioModel;

    @Override
    public void onCreate() {
        super.onCreate();
        LBApplication.get(this).applicationComponent().inject(this);

        initMediaPlayer();
    }

    private void initMediaPlayer() {
        mediaPlayer = ExoPlayer.Factory.newInstance(1);
        rxMediaPlayer = RxMediaPlayer.use(mediaPlayer);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public int getCurrentPosition() {
        return (int) mediaPlayer.getCurrentPosition();
    }

    public int getCurrentBuffer() {
        return (int) mediaPlayer.getBufferedPosition();
    }

    private void play() {
        rxMediaPlayer.preparedMediaPlayer().play().subscribe(exoPlayer -> {
            LBApplication.BUS.post(new PlayEvent());
        });
    }

    public void pause() {
        rxMediaPlayer.preparedMediaPlayer().pause().subscribe(exoPlayer -> {
            LBApplication.BUS.post(new PauseEvent());
        });
    }

    public void setTimer(int seconds) {
        // todo
    }

    public void play(int index) {
        Track track = timeline.getPlaylistTracks().get(index);
        timeline.setIndex(index);
        String url = track.getUrl();
        if (url == null || url.isEmpty()) {
            getTrackUrl(track, true);
        } else {
            PreparedMediaPlayer preparedMediaPlayer = rxMediaPlayer.from(this, url);
            Observable<ExoPlayer> observable;
            if (timeline.isAutoplay()) {
                observable = preparedMediaPlayer.play();
            } else {
                observable = preparedMediaPlayer.observable();
            }
            observable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        }
    }

    private void getTrackUrl(Track trackToFind, boolean autoPlay) {
        timeline.setAutoplay(autoPlay);
        if (trackToFind.isLocal()) {
            if (timeline.getCurrentTrack() != null) {
                timeline.getCurrentTrack().setUrl(trackToFind.getUrl());
                play();
            } else {
                next();
            }
            return;
        }
        if (!NetworkUtils.isOnline()) {
            LBApplication.BUS.post(new NetworkStateChangeEvent());
            return;
        }
        VkSimpleCallback<VkTrack> callback = new VkSimpleCallback<VkTrack>() {
            @Override
            public void success(VkTrack track) {
                if (track != null) {
                    Track currentTrack = timeline.getCurrentTrack();
                    if (currentTrack != null) {
                        currentTrack.setUrl(track.getUrl());
                        currentTrack.setAudioId(track.getAudioId());
                        currentTrack.setOwnerId(track.getOwnerId());
                        play(timeline.getIndex());
                    } else {
                        next();
                    }
                } else {
                    next();
                }
            }

            @Override
            public void failure(VkError error) {
                next();
            }
        };
        vkAudioModel.getTrack(trackToFind, 0, callback);
    }

    public int getDuration() {
        return (int) mediaPlayer.getDuration();
    }

    public void updateWidgets() {

    }

    public void playPause() {
        if (mediaPlayer.getPlayWhenReady()) {
            pause();
        } else {
            play();
        }
    }

    public void next() {
        int nextTrackIndex = timeline.getNextIndex();
        play(nextTrackIndex);
    }

    public void prev() {
        int prevTrackIndex = timeline.getPrevTrackIndex();
        play(prevTrackIndex);
    }

    public void seekTo(int position) {
        rxMediaPlayer.preparedMediaPlayer().seekTo(position).subscribe();
    }

    public void startPlayProgressUpdater() {

    }

    public void stopPlayProgressUpdater() {

    }

    public void changeUrl(int newPosition) {

    }

    public int getCurrentPositionPercent() {
        return (int) (mediaPlayer.getCurrentPosition() * 100 / mediaPlayer.getDuration());
    }

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}

package com.pillowapps.liqear.audio;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlayer;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.VkAudioSearchActivity;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.events.ArtistInfoEvent;
import com.pillowapps.liqear.entities.events.BufferizationEvent;
import com.pillowapps.liqear.entities.events.ExitEvent;
import com.pillowapps.liqear.entities.events.PauseEvent;
import com.pillowapps.liqear.entities.events.PlayEvent;
import com.pillowapps.liqear.entities.events.PlayWithoutIconEvent;
import com.pillowapps.liqear.entities.events.TimeEvent;
import com.pillowapps.liqear.entities.events.TrackAndAlbumInfoUpdatedEvent;
import com.pillowapps.liqear.entities.events.TrackInfoEvent;
import com.pillowapps.liqear.entities.events.UpdatePositionEvent;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmImage;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.CompatIcs;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.PreferencesScreenManager;
import com.pillowapps.liqear.helpers.ShakeManager;
import com.pillowapps.liqear.helpers.TrackUtils;
import com.pillowapps.liqear.models.AudioPlayerModel;
import com.pillowapps.liqear.models.TickModel;
import com.pillowapps.liqear.models.TrackNotificationModel;
import com.pillowapps.liqear.models.lastfm.LastfmAlbumModel;
import com.pillowapps.liqear.models.lastfm.LastfmArtistModel;
import com.pillowapps.liqear.models.lastfm.LastfmTrackModel;
import com.pillowapps.liqear.models.vk.VkAudioModel;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

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

    private CompositeSubscription completeSubscription = new CompositeSubscription();
    private CompositeSubscription updatersSubscription = new CompositeSubscription();
    private CompositeSubscription timerSubscription = new CompositeSubscription();
    private CompositeSubscription shakeSubscription = new CompositeSubscription();
    private CompositeSubscription trackLoadingSubscription = new CompositeSubscription();

    @Inject
    AudioPlayerModel audioPlayerModel;

    @Inject
    Timeline timeline;

    @Inject
    TickModel tickModel;

    @Inject
    LastfmTrackModel lastfmTrackModel;

    @Inject
    LastfmArtistModel lastfmArtistModel;

    @Inject
    TrackNotificationModel trackNotificationModel;

    @Inject
    LastfmAlbumModel lastfmAlbumModel;

    @Inject
    VkAudioModel vkAudioModel;

    @Inject
    AuthorizationInfoManager authorizationInfoManager;

    @Inject
    PreferencesScreenManager preferencesManager;

    @Inject
    ShakeManager shakeManager;

    @Override
    public void onCreate() {
        super.onCreate();
        LBApplication.get(this).applicationComponent().inject(this);

        restore();
        initAudioPlayer();
        updateShake();
    }

    public void restore() {
        Track currentTrack = timeline.getCurrentTrack();
        Timber.d("RESTORING " + currentTrack);
        if (currentTrack == null) {
            return;
        }
        getTrackInfo(currentTrack);
        getArtistInfo(currentTrack.getArtist(), authorizationInfoManager.getLastfmName());
    }

    private void updateShake() {
        if (preferencesManager.isShakeEnabled()) {
            shakeSubscription.add(shakeManager.initShakeDetector()
                    .subscribe(o -> {
                        next();
                    }));
        } else {
            shakeSubscription.clear();
            shakeManager.destroyShake();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) {
                return START_STICKY;
            }
            Timber.d("action = " + action);
            switch (action) {
                case ACTION_PLAY:
                    play();
                    break;
                case ACTION_PAUSE:
                    pause();
                    break;
                case ACTION_PLAY_PAUSE:
                    playPause();
                    break;
                case ACTION_CLOSE:
                    exit();
                    break;
                case ACTION_NEXT:
                    next();
                    break;
                case ACTION_PREV:
                    prev();
                    break;
                case ACTION_SHUFFLE:
                    timeline.toggleShuffle();
                    updateWidgets();
                    break;
                case ACTION_REPEAT:
                    timeline.toggleRepeat();
                    updateWidgets();
                    break;
                case ACTION_ADD_TO_VK: {
                    Track track = timeline.getCurrentTrack();
                    if (track == null) {
                        break;
                    }
                    Intent searchVkIntent = new Intent(getBaseContext(), VkAudioSearchActivity.class);
                    searchVkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    searchVkIntent.putExtra(Constants.TARGET, TrackUtils.getNotation(track));
                    getApplication().startActivity(searchVkIntent);
                    break;
                }
                case ACTION_ADD_TO_VK_FAST: {
                    final Track track = timeline.getCurrentTrack();
                    if (track == null) {
                        break;
                    }
                    vkAudioModel.addToUserAudioFast(TrackUtils.getNotation(track),
                            new VkSimpleCallback<VkResponse>() {
                                @Override
                                public void success(VkResponse data) {
                                    track.setAddedToVk(true);
                                    Toast.makeText(MusicService.this,
                                            R.string.added, Toast.LENGTH_SHORT).show();
                                    updateTrackNotification();
                                }

                                @Override
                                public void failure(VkError error) {

                                }
                            });
                    break;
                }
                case ACTION_LOVE: {
                    final Track track = timeline.getCurrentTrack();
                    if (track == null) {
                        break;
                    }
                    if (!track.isLoved()) {
                        lastfmTrackModel.love(track, new SimpleCallback<Object>() {
                            @Override
                            public void success(Object o) {
                                track.setLoved(true);
                                updateWidgets();
                                updateTrackNotification();
                            }

                            @Override
                            public void failure(String error) {
                            }
                        });
                    } else {
                        lastfmTrackModel.unlove(track, new SimpleCallback<Object>() {
                            @Override
                            public void success(Object o) {
                                track.setLoved(false);
                                updateWidgets();
                                updateTrackNotification();
                            }

                            @Override
                            public void failure(String error) {
                            }
                        });
                    }
                    break;
                }
                case CHANGE_SHAKE_PREFERENCE:
                    updateShake();
                    break;
                default:
                    throw new RuntimeException("Unknown action sent for MusicService");
            }
        }

        return START_STICKY;
    }

    private void initAudioPlayer() {
        Timber.e("init audio player");
        audioPlayerModel.seekTo(timeline.getPosition());
        completeSubscription.add(
                audioPlayerModel.addListener().subscribe(playbackState -> {
                    if (playbackState == ExoPlayer.STATE_ENDED) {
                        Timber.d("Track completed. Starting next.");
                        next();
                    } else if (playbackState == ExoPlayer.STATE_READY) {
                        timeline.setCurrentTrackDuration(audioPlayerModel.getDuration());
                        boolean playReady = audioPlayerModel.isPlayReady();
                        if (playReady) {
                            startUpdaters();
                            Timber.d("Start updaters");
                        } else {
                            stopUpdaters();
                            Timber.d("Stop updaters");
                        }
                    }
                })
        );
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        completeSubscription.clear();
        timerSubscription.clear();
        updatersSubscription.clear();
        shakeSubscription.clear();
        trackLoadingSubscription.clear();

        tickModel.close();
        audioPlayerModel.close();
        super.onDestroy();
    }

    private void exit() {
        LBApplication.BUS.post(new ExitEvent());
        stopForeground(true);
        stopSelf();
    }

    public int getCurrentPosition() {
        return audioPlayerModel.getCurrentPositionMillis();
    }

    public int getCurrentBuffer() {
        return audioPlayerModel.getCurrentBufferMillis();
    }

    private void play() {
        if (timeline.getCurrentTrack() == null) {
            return;
        }

        if (audioPlayerModel.isIdle()) {
            play(timeline.getIndex(), true);
            return;
        }

        timeline.setPlaying(true);
        audioPlayerModel.play();
        LBApplication.BUS.post(new PlayEvent());
        updateTrackNotification();
    }

    public void pause() {
        if (timeline.getCurrentTrack() == null) {
            return;
        }
        timeline.setPlaying(false);
        audioPlayerModel.pause();
        LBApplication.BUS.post(new PauseEvent());
        updateTrackNotification();
    }

    public void startUpdaters() {
        Track currentTrack = timeline.getCurrentTrack();
        if (currentTrack == null) {
            throw new IllegalStateException("Current track is null");
        }
        tickModel.startNowplayingUpdater(currentTrack);
        tickModel.startScrobblerUpdater(currentTrack);

        updatersSubscription.add(
                tickModel.getPlayProgressUpdater().observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
                    LBApplication.BUS.post(new TimeEvent());
                    LBApplication.BUS.post(new BufferizationEvent(audioPlayerModel.getCurrentBufferPercent()));
                })
        );
    }

    public void stopUpdaters() {
        updatersSubscription.clear();

        tickModel.stopScrobblerUpdater();
        tickModel.stopNowplayingUpdater();
    }

    public void setTimer(int minutes) {
        timerSubscription.add(
                tickModel.getTimer(minutes)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                            if (preferencesManager.isTimerActionPause()) {
                                pause();
                            } else {
                                exit();
                            }
                        })
        );
    }

    public void play(int index, boolean autoPlay) {
        stopUpdaters();
        tickModel.clearScrobbling();

        Track track = timeline.getPlaylistTracks().get(index);
        timeline.setIndex(index);
        timeline.listen(index);

        if (autoPlay) {
            timeline.setPlaying(true);
        }

        LBApplication.BUS.post(new UpdatePositionEvent());
        LBApplication.BUS.post(new TrackInfoEvent(track, index));

        getArtistInfo(track.getArtist(), authorizationInfoManager.getLastfmName());
        getTrackInfo(track);

        trackLoadingSubscription.clear();
        trackLoadingSubscription.add(
                audioPlayerModel.load(track).subscribe(trackInfo -> {
                    Timber.d("Track loaded " + track.toString());
                    track.setAudioId(trackInfo.getAudioId());
                    track.setOwnerId(trackInfo.getOwnerId());
                    track.setUrl(trackInfo.getUrl());
                    if (autoPlay) {
                        audioPlayerModel.play();
                        showNotificationToast();
                        updateTrackNotification();
                        LBApplication.BUS.post(new PlayEvent());
                    } else {
                        LBApplication.BUS.post(new PlayWithoutIconEvent());
                    }
                }, throwable -> {
                    Timber.e(throwable, "Error while loading track to play");
                })
        );
    }

    private void getTrackInfo(final Track track) {
        lastfmTrackModel.getTrackInfo(track, authorizationInfoManager.getLastfmName(),
                new SimpleCallback<LastfmTrack>() {
                    @Override
                    public void success(LastfmTrack lastfmTrack) {
                        Album album = Converter.convertAlbum(lastfmTrack.getAlbum());
                        Boolean loved = lastfmTrack.isLoved();
                        Intent intent = new Intent();
                        intent.setAction(Constants.ACTION_SERVICE);
                        timeline.setCurrentAlbum(album);
                        lastfmAlbumModel.getCover(album).subscribe(bitmap -> {
                            timeline.setAlbumCoverBitmap(bitmap);
                            updateTrackNotification();
                        });
                        Track currentTrack = timeline.getCurrentTrack();
                        if (currentTrack == null) {
                            throw new IllegalStateException("Current track is null");
                        }
                        currentTrack.setLoved(loved);
                        LBApplication.BUS.post(new TrackAndAlbumInfoUpdatedEvent(currentTrack, album));
                        updateWidgets();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            CompatIcs.updateRemote(MusicService.this, currentTrack);
                        }
                    }

                    @Override
                    public void failure(String errorMessage) {
                        timeline.setCurrentAlbum(null);
                        Track currentTrack = timeline.getCurrentTrack();
                        if (currentTrack == null) {
                            return;
                        }
                        currentTrack.setLoved(false);
                        LBApplication.BUS.post(new TrackAndAlbumInfoUpdatedEvent(currentTrack, null));
                    }
                });
    }

    private void getArtistInfo(final String artist, final String username) {
        lastfmArtistModel.getArtistInfo(artist, username, new SimpleCallback<LastfmArtist>() {
            @Override
            public void success(LastfmArtist lastfmArtist) {
                timeline.setPreviousArtist(artist);
                List<LastfmImage> list = lastfmArtist.getImages();
                String imageUrl = null;
                if (list.size() != 0) {
                    LastfmImage lastfmImage = list.get(list.size() - 1);
                    if (lastfmImage.getSize().isEmpty() && list.size() > 1) {
                        lastfmImage = list.get(list.size() - 2);
                    }
                    imageUrl = lastfmImage != null ? lastfmImage.getUrl() : null;
                }
                timeline.setCurrentArtistImageUrl(imageUrl);
                LBApplication.BUS.post(new ArtistInfoEvent(imageUrl));
            }

            @Override
            public void failure(String error) {
                // todo
            }
        });
    }

    public void updateTrackNotification() {
        Notification notification = trackNotificationModel.create();
        startForeground(LIQUID_BEAR_ID, notification);
    }

    private void showNotificationToast() {
        if (!preferencesManager.isToastTrackNotificationEnabled()) {
            return;
        }
        Toast.makeText(MusicService.this, TrackUtils.getNotation(timeline.getCurrentTrack()),
                Toast.LENGTH_LONG).show();
    }

    public int getDuration() {
        return audioPlayerModel.getDuration();
    }

    public void updateWidgets() {
        //todo
    }

    public void playPause() {
        if (audioPlayerModel.isPlayReady()) {
            pause();
        } else {
            play();
        }
    }

    public void next() {
        int nextTrackIndex = timeline.getNextIndex();
        play(nextTrackIndex, timeline.isPlaying());
    }

    public void prev() {
        int prevTrackIndex = timeline.getPrevTrackIndex();
        play(prevTrackIndex, timeline.isPlaying());
    }

    public void seekTo(int positionMillis) {
        audioPlayerModel.seekTo(positionMillis);
    }

    public void changeUrl(int newUrlIndex, String url) {
        // todo
        // saveUrlIndex
        // playWithUrl
    }

    public int getCurrentPositionPercent() {
        return audioPlayerModel.getCurrentPositionPercent();
    }

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}

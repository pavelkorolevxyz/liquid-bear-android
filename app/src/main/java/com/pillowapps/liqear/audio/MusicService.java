package com.pillowapps.liqear.audio;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.callbacks.SimpleCallback;
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
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.CompatIcs;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.LBPreferencesManager;
import com.pillowapps.liqear.models.AudioPlayerModel;
import com.pillowapps.liqear.models.TickModel;
import com.pillowapps.liqear.models.lastfm.LastfmArtistModel;
import com.pillowapps.liqear.models.lastfm.LastfmTrackModel;

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

    @Override
    public void onCreate() {
        super.onCreate();
        LBApplication.get(this).applicationComponent().inject(this);

        initAudioPlayer();
    }

    private void initAudioPlayer() {
        completeSubscription.add(
                audioPlayerModel.addOnComplete().subscribe(playbackState -> {
                    Timber.d("Track completed. Starting next.");
                    next();
                })
        );
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void exit() {
        completeSubscription.clear();
        timerSubscription.clear();
        updatersSubscription.clear();

        LBApplication.BUS.post(new ExitEvent());
        stopSelf();
    }

    public int getCurrentPosition() {
        return audioPlayerModel.getCurrentPositionMillis();
    }

    public int getCurrentBuffer() {
        return audioPlayerModel.getCurrentBufferMillis();
    }

    private void play() {
        timeline.setAutoplay(true);
        audioPlayerModel.play();
        LBApplication.BUS.post(new PlayEvent());
        startUpdaters();
    }

    private void startUpdaters() {
        tickModel.startNowplayingUpdater(timeline.getCurrentTrack());
        tickModel.startScrobblerUpdater(timeline.getCurrentTrack());

        updatersSubscription.add(
                tickModel.getPlayProgressUpdater().observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
                    LBApplication.BUS.post(new TimeEvent());
                    LBApplication.BUS.post(new BufferizationEvent(audioPlayerModel.getCurrentBufferMillis()));
                })
        );
    }

    public void pause() {
        audioPlayerModel.pause();
        LBApplication.BUS.post(new PauseEvent());
        stopUpdaters();
    }

    private void stopUpdaters() {
        updatersSubscription.clear();

        tickModel.stopScrobblerUpdater();
        tickModel.stopNowplayingUpdater();
    }

    public void setTimer(int seconds) {
        timerSubscription.add(
                tickModel.getTimer(seconds).subscribe(aLong -> {
                    if (LBPreferencesManager.isTimerActionPause()) {
                        pause();
                    } else {
                        exit();
                    }
                })
        );
    }

    public void play(int index) {
        stopUpdaters();
        tickModel.clearScrobbling();

        Track track = timeline.getPlaylistTracks().get(index);
        timeline.setIndex(index);

        LBApplication.BUS.post(new UpdatePositionEvent());
        LBApplication.BUS.post(new TrackInfoEvent(track));

        //todo
        getArtistInfo(track.getArtist(), AuthorizationInfoManager.getLastfmName());
        getTrackInfo(track);

        audioPlayerModel.load(track).subscribe(track1 -> {
            if (timeline.isAutoplay()) {
                audioPlayerModel.play();
                startUpdaters();
                LBApplication.BUS.post(new PlayEvent());
            } else {
                LBApplication.BUS.post(new PlayWithoutIconEvent());
            }
        }, throwable -> {
            Timber.e(throwable, "Error while loading track to play");
        });
    }

    private void getTrackInfo(final Track track) {
        lastfmTrackModel.getTrackInfo(track, AuthorizationInfoManager.getLastfmName(),
                new SimpleCallback<LastfmTrack>() {
                    @Override
                    public void success(LastfmTrack lastfmTrack) {
                        Album album = Converter.convertAlbum(lastfmTrack.getAlbum());
                        Boolean loved = lastfmTrack.isLoved();
                        Intent intent = new Intent();
                        intent.setAction(Constants.ACTION_SERVICE);
                        timeline.setCurrentAlbum(album);
//                        lastfmAlbumModel.getCover(MusicService.this, album, MusicService.this::showTrackInNotification);
                        timeline.getCurrentTrack().setLoved(loved);
                        LBApplication.BUS.post(new TrackAndAlbumInfoUpdatedEvent(album));
                        updateWidgets();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            CompatIcs.updateRemote(MusicService.this, timeline.getCurrentTrack());
                        }
                    }

                    @Override
                    public void failure(String errorMessage) {

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
            }
        });
    }

    public int getDuration() {
        return audioPlayerModel.getDuration();
    }

    public void updateWidgets() {

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
        play(nextTrackIndex);
    }

    public void prev() {
        int prevTrackIndex = timeline.getPrevTrackIndex();
        play(prevTrackIndex);
    }

    public void seekTo(int positionMillis) {
        audioPlayerModel.seekTo(positionMillis);
    }

    public void changeUrl(int newPosition) {

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

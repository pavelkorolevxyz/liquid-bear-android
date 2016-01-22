package com.pillowapps.liqear.audio;

import android.app.Notification;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.widget.Toast;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.VkAudioSearchActivity;
import com.pillowapps.liqear.callbacks.PassiveCallback;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.callbacks.VkPassiveCallback;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.RepeatMode;
import com.pillowapps.liqear.entities.ShuffleMode;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.events.ArtistInfoEvent;
import com.pillowapps.liqear.entities.events.BufferizationEvent;
import com.pillowapps.liqear.entities.events.ExitEvent;
import com.pillowapps.liqear.entities.events.NetworkStateChangeEvent;
import com.pillowapps.liqear.entities.events.PauseEvent;
import com.pillowapps.liqear.entities.events.PlayEvent;
import com.pillowapps.liqear.entities.events.PlayWithoutIconEvent;
import com.pillowapps.liqear.entities.events.PreparedEvent;
import com.pillowapps.liqear.entities.events.TimeEvent;
import com.pillowapps.liqear.entities.events.TrackAndAlbumInfoUpdatedEvent;
import com.pillowapps.liqear.entities.events.TrackInfoEvent;
import com.pillowapps.liqear.entities.events.UpdatePositionEvent;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmImage;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.CompatIcs;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.LBPreferencesManager;
import com.pillowapps.liqear.helpers.NetworkUtils;
import com.pillowapps.liqear.helpers.ShakeDetector;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.helpers.TimeUtils;
import com.pillowapps.liqear.helpers.TrackUtils;
import com.pillowapps.liqear.models.PlayingState;
import com.pillowapps.liqear.models.PlaylistModel;
import com.pillowapps.liqear.models.TrackNotification;
import com.pillowapps.liqear.models.lastfm.LastfmAlbumModel;
import com.pillowapps.liqear.models.lastfm.LastfmArtistModel;
import com.pillowapps.liqear.models.lastfm.LastfmTrackModel;
import com.pillowapps.liqear.models.vk.VkAudioModel;
import com.pillowapps.liqear.models.vk.VkStatusModel;
import com.pillowapps.liqear.widget.FourWidthOneHeightWidget;
import com.pillowapps.liqear.widget.FourWidthThreeHeightAltWidget;
import com.pillowapps.liqear.widget.FourWidthThreeHeightWidget;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener {

    private static final int LIQUID_BEAR_ID = 1314;
    private static final String AVRCP_META_CHANGED = "com.android.music.metachanged";

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
    private MediaPlayer mediaPlayer;

    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;

    private AudioManager audioManager;

    private HeadsetStateReceiver headsetStateReceiver;
    private PhoneStateListener phoneStateListener;
    private AudioManager.OnAudioFocusChangeListener focusChangeListener;

    private SensorManager sensorManager;
    private ShakeDetector shakeDetector;

    private Subscriber<Long> nowplayingSubscriber;
    private Subscriber<Long> playProgressSubscriber;
    private Subscriber<Long> timerSubscriber;

    private boolean hasDataSource = false;
    private boolean scrobbled = false;
    private int secondsTrackPlayed = 0;

    private boolean urlChanged;
    private int urlNumber;
    private boolean prepared;

    private int currentBuffer;

    @Inject
    VkAudioModel vkAudioModel;

    @Inject
    LastfmTrackModel lastfmTrackModel;

    @Inject
    VkStatusModel vkStatusModel;

    @Inject
    LastfmArtistModel lastfmArtistModel;

    @Inject
    LastfmAlbumModel lastfmAlbumModel;

    @Override
    public void onCreate() {
        super.onCreate();
        LBApplication.get(this).applicationComponent().inject(this);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);

        initMediaPlayer();
        initLocks();
        initRemote();
        initStateReceivers();
        initWidgets();

        restoreService();

        if (LBPreferencesManager.isShakeEnabled()) {
            initShakeDetector();
        }
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setAudioSessionId(LIQUID_BEAR_ID);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    }

    private void initLocks() {
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getString(R.string.app_name));
        WifiManager wifimanager = (WifiManager) LBApplication.getAppContext().getSystemService(Context.WIFI_SERVICE);
        wifiLock = wifimanager.createWifiLock(getString(R.string.app_name));
    }

    private void initRemote() {
        final Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            CompatIcs.registerRemote(this, audioManager);
            if (currentTrack != null) {
                CompatIcs.updateRemote(MusicService.this, currentTrack);
            }
        } else {
            MediaButtonReceiver.registerMediaButton(this);
        }
    }

    private void unregisterRemote() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            CompatIcs.unregisterRemote(MusicService.this, audioManager);
        } else {
            MediaButtonReceiver.unregisterMediaButton(this);
        }
    }

    private void initStateReceivers() {
        headsetStateReceiver = new HeadsetStateReceiver();
        IntentFilter receiverFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        LBApplication.getAppContext().registerReceiver(headsetStateReceiver, receiverFilter);

        focusChangeListener = focusChange -> {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                if (Timeline.getInstance().getPlayingState() == PlayingState.PLAYING) {
                    pause();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        CompatIcs.unregisterRemote(MusicService.this, audioManager);
                    } else {
                        MediaButtonReceiver.unregisterMediaButton(MusicService.this);
                    }
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (Timeline.getInstance().getPlayingStateBeforeCall() == PlayingState.PLAYING) {
                    play();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    CompatIcs.registerRemote(MusicService.this, audioManager);
                    if (Timeline.getInstance().getCurrentTrack() != null) {
                        CompatIcs.updateRemote(MusicService.this,
                                Timeline.getInstance().getCurrentTrack());
                    }
                } else {
                    MediaButtonReceiver.registerMediaButton(MusicService.this);
                }
            }
        };
        audioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        registerPhoneCallReceiver();
    }

    private void registerPhoneCallReceiver() {
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        pause();
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (Timeline.getInstance().getPlayingStateBeforeCall() == PlayingState.PLAYING) {
                            play();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        pause();
                        break;
                    default:
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        changePhoneCallReceiverListener(PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void initShakeDetector() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector();
        shakeDetector.setOnShakeListener(count -> next());
        sensorManager.registerListener(shakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    private void destroyShake() {
        if (sensorManager != null && shakeDetector != null) {
            sensorManager.unregisterListener(shakeDetector);
        }
    }

    private void unregisterPhoneCallReceiver() {
        changePhoneCallReceiverListener(PhoneStateListener.LISTEN_NONE);
    }

    private void changePhoneCallReceiverListener(int listenCallState) {
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, listenCallState);
        }
    }

    private void restoreService() {
        EqualizerManager.restoreEqualizer();
        EqualizerManager.setEnabled(SharedPreferencesManager.getEqualizerPreferences().getBoolean("enabled", true));

        final SharedPreferences urlNumberPreferences = SharedPreferencesManager.getUrlNumberPreferences();
        final Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (currentTrack == null) return;
        urlNumber = urlNumberPreferences.getInt(TrackUtils.getNotation(currentTrack), 0);
        Timeline.getInstance().addToPrevIndexes(Timeline.getInstance().getIndex());
        getTrackUrl(currentTrack, false, urlNumber);
    }

    private void initWidgets() {
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        FourWidthOneHeightWidget.checkEnabled(this, manager);
        FourWidthThreeHeightWidget.checkEnabled(this, manager);
        FourWidthThreeHeightAltWidget.checkEnabled(this, manager);
    }

    public void updateWidgets() {
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        boolean playing = Timeline.getInstance().getPlayingState() == PlayingState.PLAYING;
        FourWidthOneHeightWidget.updateWidget(this, manager, playing);
        FourWidthThreeHeightWidget.updateWidget(this, manager, playing);
        FourWidthThreeHeightAltWidget.updateWidget(this, manager, playing);
    }

    @Override
    public void onDestroy() {
        Timeline.getInstance().setPlayingState(PlayingState.DEFAULT);
        updateWidgets();
        manualDestroy();
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        super.onDestroy();
    }

    private void manualDestroy() {
        releaseLocks();
        destroyShake();

        audioManager.abandonAudioFocus(focusChangeListener);
        saveState();
        mediaPlayer.release();

        if (headsetStateReceiver != null) {
            LBApplication.getAppContext().unregisterReceiver(headsetStateReceiver);
        }

        unregisterRemote();

        EqualizerManager.releaseEqualizer();

        unregisterPhoneCallReceiver();
        stopForeground(true);
    }

    public void exit() {
        LBApplication.BUS.post(new ExitEvent());
        stopSelf();
    }

    private void releaseLocks() {
        if (wakeLock.isHeld()) wakeLock.release();
        if (wifiLock.isHeld()) wifiLock.release();
    }

    private void acquireLocks() {
        wakeLock.acquire();
        wifiLock.acquire();
    }

    private void saveState() {
        saveTrackState();
        SharedPreferences.Editor editor = SharedPreferencesManager.getPreferences().edit();
        editor.putInt(Constants.CURRENT_POSITION, getCurrentPosition());
        editor.putBoolean(Constants.SHUFFLE_MODE_ON,
                Timeline.getInstance().getShuffleMode() == ShuffleMode.SHUFFLE);
        editor.putBoolean(Constants.REPEAT_MODE_ON,
                Timeline.getInstance().getRepeatMode() == RepeatMode.REPEAT);
        editor.apply();
    }

    private void saveTrackState() {
        SharedPreferences.Editor editor = SharedPreferencesManager.getPreferences().edit();
        final Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (Timeline.getInstance().getPlaylistTracks() != null
                && Timeline.getInstance().getPlaylistTracks().size() != 0
                && currentTrack != null) {
            editor.putString(Constants.ARTIST, currentTrack.getArtist());
            editor.putString(Constants.TITLE, currentTrack.getTitle());
            editor.putInt(Constants.DURATION, currentTrack
                    .getDuration());
        }
        editor.putInt(Constants.CURRENT_INDEX, Timeline.getInstance().getIndex());
        editor.apply();
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) return START_STICKY;
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
                    Timeline.getInstance().toggleShuffle();
                    updateWidgets();
                    break;
                case ACTION_REPEAT:
                    Timeline.getInstance().toggleRepeat();
                    updateWidgets();
                    break;
                case ACTION_ADD_TO_VK: {
                    Track track = Timeline.getInstance().getCurrentTrack();
                    if (track != null) {
                        Intent searchVkIntent = new Intent(getBaseContext(),
                                VkAudioSearchActivity.class);
                        searchVkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        searchVkIntent.putExtra(Constants.TARGET, TrackUtils.getNotation(track));
                        getApplication().startActivity(searchVkIntent);
                    }
                    break;
                }
                case ACTION_ADD_TO_VK_FAST: {
                    final Track track = Timeline.getInstance().getCurrentTrack();
                    if (track != null) {
                        vkAudioModel.addToUserAudioFast(TrackUtils.getNotation(track),
                                new VkSimpleCallback<VkResponse>() {
                                    @Override
                                    public void success(VkResponse data) {
                                        track.setAddedToVk(true);
                                        Toast.makeText(LBApplication.getAppContext(),
                                                R.string.added, Toast.LENGTH_SHORT).show();
                                        showTrackInNotification();
                                    }

                                    @Override
                                    public void failure(VkError error) {

                                    }
                                });
                    }
                    break;
                }
                case ACTION_LOVE: {
                    final Track track = Timeline.getInstance().getCurrentTrack();
                    if (track != null) {
                        if (!track.isLoved()) {
                            lastfmTrackModel.love(track, new SimpleCallback<Object>() {
                                @Override
                                public void success(Object o) {
                                    track.setLoved(true);
                                    updateWidgets();
                                    showTrackInNotification();
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
                                    showTrackInNotification();
                                }

                                @Override
                                public void failure(String error) {
                                }
                            });
                        }
                    }
                    break;
                }
                case CHANGE_SHAKE_PREFERENCE:
                    if (LBPreferencesManager.isShakeEnabled()) {
                        initShakeDetector();
                    } else {
                        destroyShake();
                    }
                    break;
            }
        }

        return START_STICKY;
    }

    public void playPause() {
        Timeline.getInstance().setStartPlayingOnPrepared(true);
        if (hasDataSource()) {
            togglePlayPause();
        } else {
            Timeline.getInstance().setPlayingState(PlayingState.PLAYING);
            new PlaylistModel().getMainPlaylist(MusicService.this)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(playlist -> {
                        Timeline.getInstance().setPlaylist(playlist);
                        List<Track> tracks = playlist.getTracks();

                        SharedPreferences preferences = SharedPreferencesManager.getPreferences();
                        String artist = preferences.getString(Constants.ARTIST, "");
                        String title = preferences.getString(Constants.TITLE, "");
                        int currentIndex = preferences.getInt(Constants.CURRENT_INDEX, 0);
                        int position = preferences.getInt(Constants.CURRENT_POSITION, 0);

                        boolean currentFits = currentIndex < tracks.size();
                        if (!currentFits) currentIndex = 0;
                        if (currentIndex == 0 && tracks.size() == 0) return;
                        Track currentTrack = tracks.get(currentIndex);
                        boolean tracksEquals = currentFits
                                && (artist + title).equalsIgnoreCase(currentTrack.getArtist()
                                + currentTrack.getTitle());
                        if (!tracksEquals) {
                            currentIndex = 0;
                            position = 0;
                        }
                        Timeline.getInstance().setIndex(currentIndex);
                        if (currentIndex > tracks.size()) {
                            position = 0;
                        }
                        if (!LBPreferencesManager.isContinueFromLastPositionEnabled()) {
                            position = 0;
                        }

                        Timeline.getInstance().setTimePosition(position);
                        if (tracks.size() != 0
                                && Timeline.getInstance().getCurrentTrack() == null) {
                            play();
                        }
                    });
        }
    }

    private boolean hasDataSource() {
        return hasDataSource;
    }

    public void showTrackInNotification() {
        Track track = Timeline.getInstance().getCurrentTrack();
        if (track == null) return;
        Notification notification = new TrackNotification().create(this, track);
        bluetoothNotifyChange(AVRCP_META_CHANGED, track);
        startForeground(LIQUID_BEAR_ID, notification);
    }

    private void showNotificationToast() {
        if (LBPreferencesManager.isToastTrackNotificationEnabled()) {
            Toast.makeText(MusicService.this,
                    Html.fromHtml(TrackUtils.getNotation(Timeline.getInstance().getCurrentTrack())),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void bluetoothNotifyChange(String what, Track track) {
        Intent i = new Intent(what);
        int duration = getDuration();
        if (track != null && duration != 0) {
            i.putExtra("PLAYER_PACKAGE_NAME", "com.pillowapps.liqear");
            i.putExtra("artist", track.getArtist());
            i.putExtra("track", track.getTitle());
            i.putExtra("duration", duration);
            i.putExtra("playing", Timeline.getInstance().getPlayingState() == PlayingState.PLAYING);
            sendBroadcast(i);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        this.currentBuffer = percent;
        LBApplication.BUS.post(new BufferizationEvent(percent));
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
//        currentPositionPercent = 0;
        if (Timeline.getInstance().getPlaylistTracks().size() == 0) {
            return;
        }
        prepared = true;
        LBApplication.BUS.post(new PreparedEvent());
        Track currentTrack = Timeline.getInstance().getCurrentTrack();
        currentTrack.setDuration(mediaPlayer.getDuration());
        hasDataSource = true;
        scrobbled = false;
        secondsTrackPlayed = 0;
        if (NetworkUtils.isOnline()) {
            if (!currentTrack.getArtist().equals(Timeline.getInstance().getPreviousArtist())) {
                getArtistInfo(currentTrack.getArtist(), AuthorizationInfoManager.getLastfmName());
            }
            getTrackInfo(currentTrack);
        }
        saveTrackState();
        if (Timeline.getInstance().isStartPlayingOnPrepared()) {
            Timeline.getInstance().setPlayingState(PlayingState.PLAYING);
            mediaPlayer.start();
            startUpdaters();
            LBApplication.BUS.post(new PlayEvent());
            startPlayProgressUpdater();
            showTrackInNotification();
        } else {
            LBApplication.BUS.post(new PlayWithoutIconEvent());
            LBApplication.BUS.post(new UpdatePositionEvent());
        }
        int result = audioManager.requestAudioFocus(focusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            if (Timeline.getInstance().getPlayingStateBeforeCall() == PlayingState.PLAYING) {
                play();
            }
            initRemote();
        }
    }

    public void togglePlayPause() {
        if (Timeline.getInstance().getPlayingState() == PlayingState.PLAYING) {
            pause();
        } else {
            play();
        }
    }

    public void play() {
        Timeline.getInstance().setPlayingState(PlayingState.PLAYING);
        acquireLocks();
        if (hasDataSource) {
            mediaPlayer.start();
            showTrackInNotification();
            startUpdaters();
            Timeline.getInstance().setStartPlayingOnPrepared(true);
            startPlayProgressUpdater();
            LBApplication.BUS.post(new PlayEvent());
        }
    }

    public void pause() {
        Timeline.getInstance().setPlayingState(PlayingState.DEFAULT);
        releaseLocks();
        if (hasDataSource) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            showTrackInNotification();
            LBApplication.BUS.post(new PauseEvent());
        }
        stopUpdaters();
    }

    public void play(int index) {
        Timeline.getInstance().setIndex(index);
        urlNumber = 0;
        updateWidgets();

//        Timeline.getInstance().addToPrevClicked(Timeline.getInstance().getIndex());
        initRemote();
        Timeline.getInstance().setPlaylistChanged(false);
        final Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (currentTrack == null) return;
        Timeline.getInstance().addToPrevIndexes(Timeline.getInstance().getIndex());
//            Timeline.getInstance().getPlaylistTracks().get(Timeline.getInstance().getPreviousTracksIndexes().peek())
//                    .setCurrent(false);

        acquireLocks();
        if (currentTrack.getUrl() != null && !currentTrack.getUrl().isEmpty()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(currentTrack.getUrl());
                prepared = false;
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                onError(mediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, MediaPlayer.MEDIA_ERROR_UNKNOWN);
            }
        } else {
            final SharedPreferences urlNumberPreferences =
                    SharedPreferencesManager.getUrlNumberPreferences();
            if (urlChanged) {
                urlChanged = false;
            } else {
                urlNumber = urlNumberPreferences.getInt(TrackUtils.getNotation(currentTrack), 0);
            }
            getTrackUrl(currentTrack, true, urlNumber);
        }
        if (Timeline.getInstance().isStartPlayingOnPrepared()) {
            showNotificationToast();
            showTrackInNotification();
            LBApplication.BUS.post(new PlayEvent());
        } else {
            LBApplication.BUS.post(new PlayWithoutIconEvent());
        }
        LBApplication.BUS.post(new TrackInfoEvent());
    }

    private void playWithUrl() {
        initRemote();
        Timeline.getInstance().setPlaylistChanged(false);
        final Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (currentTrack == null) return;
        Timeline.getInstance().addToPrevIndexes(Timeline.getInstance().getIndex());
//            Timeline.getInstance().getPlaylistTracks().get(Timeline.getInstance().getPreviousTracksIndexes().peek()).setCurrent(false);
        acquireLocks();
        String url = TrackUtils.getUrlFromTrack(currentTrack);
        if (url != null && !url.isEmpty()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(url);
                prepared = false;
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                onError(mediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, MediaPlayer.MEDIA_ERROR_UNKNOWN);
            }
        }
        if (Timeline.getInstance().isStartPlayingOnPrepared()) {
            showNotificationToast();
            showTrackInNotification();
            LBApplication.BUS.post(new PlayEvent());
        } else {
            LBApplication.BUS.post(new PlayWithoutIconEvent());
        }
        LBApplication.BUS.post(new TrackInfoEvent());
    }

    public void next() {
        stopPlayProgressUpdater();
//        SharedPreferencesManager.getPreferences().edit().putInt(Constants.CURRENT_POSITION, 0).commit();
        List<Track> playlist = Timeline.getInstance().getPlaylistTracks();
        if (playlist == null) return;
        switch (Timeline.getInstance().getRepeatMode()) {
            case REPEAT_PLAYLIST:
                int tracklistSize = playlist.size();
                if (tracklistSize == 0) {
                    return;
                }
                int currentIndex = Timeline.getInstance().getIndex();
                switch (Timeline.getInstance().getShuffleMode()) {
                    case DEFAULT:
                        currentIndex = (currentIndex + 1) % tracklistSize;
                        break;
                    case SHUFFLE:
                        currentIndex = Timeline.getInstance().getRandomIndex();
                        break;
                    default:
                        break;
                }
                LinkedList<Integer> queue = Timeline.getInstance().getQueueIndexes();
                if (queue != null && !queue.isEmpty()) {
                    currentIndex = queue.poll();
                }
                Timeline.getInstance().setIndex(currentIndex);
                updateWidgets();
                break;
            case REPEAT:
            default:
                break;
        }
        play(Timeline.getInstance().getIndex());
    }

    public void prev() {
        stopPlayProgressUpdater();
        SharedPreferencesManager.getPreferences().edit().putInt(Constants.CURRENT_POSITION, 0).commit();
        Stack<Integer> prevTracksIndexes = Timeline.getInstance().getPreviousTracksIndexes();
        if (prevTracksIndexes.size() == 0) {
            return;
        }

        if (prevTracksIndexes.size() > 1) {
            prevTracksIndexes.pop();
        }

        int prevPosition = prevTracksIndexes.peek();
        Timeline.getInstance().setIndex(prevPosition);
        updateWidgets();
        play();
    }

    public void changeUrl(int urlNumber) {
        this.urlNumber = urlNumber;
        urlChanged = true;
        final SharedPreferences urlNumberPreferences = SharedPreferencesManager.getUrlNumberPreferences();
        final SharedPreferences.Editor editor = urlNumberPreferences.edit();
        final Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (currentTrack == null) return;
        final String notation = TrackUtils.getNotation(currentTrack);
        editor.putInt(notation, urlNumber);
        editor.apply();
        currentTrack.setUrl(null);
        play(Timeline.getInstance().getIndex());
    }

    public void seekTo(int position) {
        if (position > mediaPlayer.getDuration()) {
            position = 0;
        }
        mediaPlayer.seekTo(position);
        Timeline.getInstance().setTimePosition(position);
    }

    public int getDuration() {
        int duration = 0;
        if (prepared) {
            try {
                duration = mediaPlayer.getDuration();
            } catch (IllegalStateException ignored) {
            }
        }
        if (duration > 2 * 60 * 60 * 1000 || duration < 0) return 0;
        return duration;
    }

    public int getCurrentPosition() {
        if (mediaPlayer == null) return 0;
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (IllegalStateException e) {
            return 0;
        }
    }

    public int getCurrentPositionPercent() {
        int duration = getDuration();
        if (duration == 0) return 0;
        return (mediaPlayer.getCurrentPosition() * 100) / duration;
    }

    private void startUpdaters() {
        startNowplayingUpdater();
        startPlayProgressUpdater();
    }

    private void stopUpdaters() {
        if (nowplayingSubscriber != null && !nowplayingSubscriber.isUnsubscribed()) {
            nowplayingSubscriber.unsubscribe();
        }
        stopPlayProgressUpdater();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void startNowplayingUpdater() {
        if (nowplayingSubscriber != null && !nowplayingSubscriber.isUnsubscribed()) {
            nowplayingSubscriber.unsubscribe();
        }
        if (NetworkUtils.isOnline() && AuthorizationInfoManager.isAuthorizedOnLastfm()
                && LBPreferencesManager.isNowplayingEnabled()
                && Timeline.getInstance().getPlaylistTracks().size() > Timeline.getInstance()
                .getIndex()) {
            nowplayingSubscriber = new Subscriber<Long>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onNext(Long aLong) {
                    updateNowPlaying(Timeline.getInstance().getCurrentTrack());
                }
            };
            Observable.interval(15, TimeUnit.SECONDS)
                    .startWith(-1L)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(nowplayingSubscriber);
        }
    }

    private void updateNowPlaying(final Track currentTrack) {
        if (SharedPreferencesManager.getPreferences().getBoolean("nowplaying_check_box_preferences", true)) {
            lastfmTrackModel.nowplaying(currentTrack, new PassiveCallback());
        }
        if (SharedPreferencesManager.getPreferences().getBoolean("nowplaying_vk_check_box_preferences", true)) {
            vkStatusModel.updateStatus(currentTrack, new VkPassiveCallback());
        }
    }

    public void startPlayProgressUpdater() {
        if (playProgressSubscriber != null && !playProgressSubscriber.isUnsubscribed()) {
            playProgressSubscriber.unsubscribe();
        }
        playProgressSubscriber = new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                LBApplication.BUS.post(new TimeEvent());
                if (scrobbled || !LBPreferencesManager.isScrobblingEnabled()) return;
                secondsTrackPlayed++;
                int duration = getDuration();
                if (duration != 0) {
                    int playedPercent = (int) (((float) secondsTrackPlayed / (duration / 1000f)) * 100);
                    if (playedPercent >= LBPreferencesManager.getPercentsToScrobble()) {
                        scrobble(Timeline.getInstance().getCurrentTrack());
                    }
                }
            }
        };
        Observable.interval(1, TimeUnit.SECONDS)
                .startWith(-1L)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(playProgressSubscriber);
    }

    public void stopPlayProgressUpdater() {
        if (playProgressSubscriber != null && !playProgressSubscriber.isUnsubscribed()) {
            playProgressSubscriber.unsubscribe();
        }
    }

    public void setTimer(int seconds) {
        if (timerSubscriber != null && !timerSubscriber.isUnsubscribed()) {
            timerSubscriber.unsubscribe();
        }
        timerSubscriber = new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                if (LBPreferencesManager.isTimerActionPause()) {
                    pause();
                } else {
                    exit();
                }
            }
        };

        Observable.timer(seconds, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timerSubscriber);
    }

    private void getTrackUrl(final Track trackToFind, final boolean current, final int urlNumber) {
        final Timeline timeline = Timeline.getInstance();
        if (trackToFind.isLocal()) {
            if (timeline.getCurrentTrack() != null) {
                timeline.getCurrentTrack().setUrl(trackToFind.getUrl());
                if (!current) {
                    Timeline.getInstance().setStartPlayingOnPrepared(false);
                }
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
                        if (!current) {
                            Timeline.getInstance().setStartPlayingOnPrepared(false);
                        }
                        playWithUrl();
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
        if (TrackUtils.vkInfoAvailable(trackToFind)) {
            vkAudioModel.getTrackById(trackToFind, urlNumber, callback);
        } else {
            vkAudioModel.getTrackByNotation(trackToFind, urlNumber, callback);
        }
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
                        Timeline.getInstance().setCurrentAlbum(album);
                        lastfmAlbumModel.getCover(MusicService.this, album, MusicService.this::showTrackInNotification);
                        Timeline.getInstance().getCurrentTrack().setLoved(loved);
                        LBApplication.BUS.post(new TrackAndAlbumInfoUpdatedEvent(album));
                        updateWidgets();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            CompatIcs.updateRemote(MusicService.this,
                                    Timeline.getInstance().getCurrentTrack());
                        }
                    }

                    @Override
                    public void failure(String errorMessage) {

                    }
                });
    }

    private void scrobble(final Track track) {
        String album = track.getAlbum();
        scrobbled = true;
        lastfmTrackModel.scrobble(track.getArtist(), track.getTitle(), album,
                TimeUtils.getCurrentTimeInSeconds(),
                new PassiveCallback());
    }

    private void getArtistInfo(final String artist, final String username) {
        lastfmArtistModel.getArtistInfo(artist, username, new SimpleCallback<LastfmArtist>() {
            @Override
            public void success(LastfmArtist lastfmArtist) {
                Timeline.getInstance().setPreviousArtist(artist);
                List<LastfmImage> list = lastfmArtist.getImages();
                String imageUrl = null;
                if (list.size() != 0) {
                    LastfmImage lastfmImage = list.get(list.size() - 1);
                    if (lastfmImage.getSize().isEmpty() && list.size() > 1) {
                        lastfmImage = list.get(list.size() - 2);
                    }
                    imageUrl = lastfmImage != null ? lastfmImage.getUrl() : null;
                }
                LBApplication.BUS.post(new ArtistInfoEvent(imageUrl));
            }

            @Override
            public void failure(String error) {
            }
        });
    }

    public boolean isPrepared() {
        return prepared;
    }

    public int getCurrentBuffer() {
        return currentBuffer;
    }

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}

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
import com.pillowapps.liqear.activities.SearchActivity;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.RepeatMode;
import com.pillowapps.liqear.entities.ShuffleMode;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.events.AlbumInfoEvent;
import com.pillowapps.liqear.entities.events.ArtistInfoEvent;
import com.pillowapps.liqear.entities.events.BufferizationEvent;
import com.pillowapps.liqear.entities.events.ExitEvent;
import com.pillowapps.liqear.entities.events.NetworkStateChangeEvent;
import com.pillowapps.liqear.entities.events.PauseEvent;
import com.pillowapps.liqear.entities.events.PlayEvent;
import com.pillowapps.liqear.entities.events.PlayWithoutIconEvent;
import com.pillowapps.liqear.entities.events.PreparedEvent;
import com.pillowapps.liqear.entities.events.TimeEvent;
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
import com.pillowapps.liqear.helpers.PlaylistManager;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.helpers.ShakeDetector;
import com.pillowapps.liqear.helpers.TrackUtils;
import com.pillowapps.liqear.helpers.Utils;
import com.pillowapps.liqear.models.PlayingState;
import com.pillowapps.liqear.models.TrackNotification;
import com.pillowapps.liqear.models.lastfm.LastfmAlbumModel;
import com.pillowapps.liqear.models.lastfm.LastfmArtistModel;
import com.pillowapps.liqear.models.lastfm.LastfmTrackModel;
import com.pillowapps.liqear.models.vk.VkAudioModel;
import com.pillowapps.liqear.models.vk.VkStatusModel;
import com.pillowapps.liqear.network.callbacks.CompletionCallback;
import com.pillowapps.liqear.network.callbacks.PassiveCallback;
import com.pillowapps.liqear.network.callbacks.SimpleCallback;
import com.pillowapps.liqear.network.callbacks.VkPassiveCallback;
import com.pillowapps.liqear.network.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.widget.FourWidthOneHeightWidget;
import com.pillowapps.liqear.widget.FourWidthThreeHeightAltWidget;
import com.pillowapps.liqear.widget.FourWidthThreeHeightWidget;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener {

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

    private static final int LIQUID_BEAR_ID = 1314;
    private static final String AVRCP_META_CHANGED = "com.android.music.metachanged";

    private final IBinder binder = new LocalBinder();
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private AudioManager manager;
    private boolean hasDataSource = false;

    private HeadsetStateReceiver headsetReceiver;

    private PhoneStateListener phoneStateListener;
    private PowerManager.WakeLock wakeLock;

    private WifiManager.WifiLock wifiLock;
    private SensorManager sensorManager;

    private int urlNumber;
    private boolean urlChanged;

    private ShakeDetector shakeDetector;
    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                            || focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        if (Timeline.getInstance().getPlayingState() == PlayingState.PLAYING) {
                            pause();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                CompatIcs.unregisterRemote(MusicService.this, manager);
                            } else {
                                MediaButtonReceiver.unregisterMediaButton(MusicService.this);
                            }
                        }
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        if (Timeline.getInstance().getPlayingStateBeforeCall() == PlayingState.PLAYING) {
                            play();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            CompatIcs.registerRemote(MusicService.this, manager);
                            if (Timeline.getInstance().getCurrentTrack() != null) {
                                CompatIcs.updateRemote(MusicService.this,
                                        Timeline.getInstance().getCurrentTrack());
                            }
                        } else {
                            MediaButtonReceiver.registerMediaButton(MusicService.this);
                        }
                    }
                }
            };
    private boolean playOnPrepared;
    private Subscriber<Long> playProgressSubscriber;
    private Subscriber<Long> nowplayingSubscriber;
    private Subscriber<Long> timerSubscriber;
    private boolean prepared;
    private int currentBuffer = 0;
    private int currentPositionPercent = 0;
    private boolean scrobbled = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getString(R.string.app_name));
        WifiManager wifimanager = (WifiManager) LBApplication.getAppContext()
                .getSystemService(Context.WIFI_SERVICE);
        wifiLock = wifimanager.createWifiLock("player_on");
        manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        final Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            CompatIcs.registerRemote(this, manager);
            if (currentTrack != null) {
                CompatIcs.updateRemote(MusicService.this, currentTrack);
            }
        } else {
            MediaButtonReceiver.registerMediaButton(this);
        }
        headsetReceiver = new HeadsetStateReceiver();
        IntentFilter receiverFilter = new IntentFilter(
                AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        try {
            LBApplication.getAppContext().registerReceiver(headsetReceiver, receiverFilter);
        } catch (IllegalArgumentException ignored) {
        }
        manager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        registerPhoneCallReceiver();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setAudioSessionId(LIQUID_BEAR_ID);
        manager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        restoreService();
        initWidgets();
//        connectionReceiver = new ConnectionChangeReceiver();
//        registerReceiver(connectionReceiver, new IntentFilter(Constants.NETWORK_ACTION));
        if (PreferencesManager.getPreferences().getBoolean("shake_next", false)) {
            initShakeDetector();
        }
    }

    private void saveState() {
        saveTrackState();
        SharedPreferences.Editor editor = PreferencesManager.getPreferences().edit();
        editor.putInt(Constants.CURRENT_POSITION, getCurrentPosition());
        editor.putBoolean(Constants.SHUFFLE_MODE_ON,
                Timeline.getInstance().getShuffleMode() == ShuffleMode.SHUFFLE);
        editor.putBoolean(Constants.REPEAT_MODE_ON,
                Timeline.getInstance().getRepeatMode() == RepeatMode.REPEAT);
        editor.apply();
    }

    private void saveTrackState() {
        SharedPreferences.Editor editor = PreferencesManager.getPreferences().edit();
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

    private void restoreService() {
        EqualizerManager.restoreEqualizer();
        EqualizerManager.setEnabled(PreferencesManager.getEqualizerPreferences()
                .getBoolean("enabled", true));
        final SharedPreferences urlNumberPreferences = PreferencesManager.getUrlNumberPreferences();
        final Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (currentTrack == null) return;
//        urlNumber = urlNumberPreferences.getInt(currentTrack.getNotation(), 0);
//        if (AudioTimeline.hasSaves()) {
//            AudioTimeline.addToPrevIndexes(AudioTimeline.getCurrentIndex());
//            AudioTimeline.addToPrevClicked(AudioTimeline.getCurrentIndex());
//            getTrackUrl(currentTrack, false, urlNumber);
//        }
    }

    @Override
    public void onDestroy() {
        Timeline.getInstance().setPlayingState(PlayingState.DEFAULT);
        updateWidgets();
        destroy();
        manager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        super.onDestroy();
    }

    private void destroy() {
        releaseLocks();
        destroyShake();
//        unregisterReceiver(connectionReceiver);
        manager.abandonAudioFocus(focusChangeListener);
        saveState();
        stopForeground(true);
        try {
            mediaPlayer.release();
        } catch (Exception ignored) {
        }
        if (headsetReceiver != null) {
            LBApplication.getAppContext().unregisterReceiver(headsetReceiver);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            CompatIcs.unregisterRemote(MusicService.this, manager);
        } else {
            MediaButtonReceiver.unregisterMediaButton(this);
        }
        EqualizerManager.releaseEqualizer();
        unregisterPhoneCallReceiver();
    }

    private void destroyShake() {
        try {
            sensorManager.unregisterListener(shakeDetector);
        } catch (Exception ignored) {
        }
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
                        if (Timeline.getInstance().getPlayingStateBeforeCall() == PlayingState.PLAYING)
                            play();
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

    private void unregisterPhoneCallReceiver() {
        changePhoneCallReceiverListener(PhoneStateListener.LISTEN_NONE);
    }

    private void changePhoneCallReceiverListener(int listenCallState) {
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, listenCallState);
        }
    }

    private void initShakeDetector() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector();
        shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                next();
            }
        });
        sensorManager.registerListener(shakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
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
                    setPlayOnPrepared(true);
                    if (hasDataSource) {
                        togglePlayPause();
                    } else {
                        Timeline.getInstance().setPlayingState(PlayingState.PLAYING);
                        List<Track> tracks = PlaylistManager.getInstance().loadPlaylist();
                        Timeline.getInstance().setPlaylist(new Playlist(tracks));
                        SharedPreferences preferences = PreferencesManager.getPreferences();
                        String artist = preferences.getString(Constants.ARTIST, "");
                        String title = preferences.getString(Constants.TITLE, "");
                        int currentIndex = preferences.getInt(Constants.CURRENT_INDEX, 0);
                        int position = preferences.getInt(Constants.CURRENT_POSITION, 0);

                        boolean currentFits = currentIndex < tracks.size();
                        if (!currentFits) currentIndex = 0;
                        if (currentIndex == 0 && tracks.size() == 0) return START_STICKY;
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
                        if (!PreferencesManager.getPreferences()
                                .getBoolean("continue_from_position", true)) {
                            position = 0;
                        }

                        Timeline.getInstance().setTimePosition(position);
                        if (tracks.size() != 0
                                && Timeline.getInstance().getCurrentTrack() == null) {
                            play();
                        }
                    }
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
                                SearchActivity.class);
                        searchVkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        searchVkIntent.putExtra(SearchActivity.SEARCH_MODE,
                                SearchActivity.SearchMode.AUDIO_SEARCH_RESULT_ADD_VK);
                        searchVkIntent.putExtra(Constants.TARGET, TrackUtils.getNotation(track));
                        getApplication().startActivity(searchVkIntent);
                    }
                    break;
                }
                case ACTION_ADD_TO_VK_FAST: {
                    final Track track = Timeline.getInstance().getCurrentTrack();
                    if (track != null) {
                        new VkAudioModel().addToUserAudioFast(TrackUtils.getNotation(track),
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
                            new LastfmTrackModel().love(track, new SimpleCallback<Object>() {
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
                            new LastfmTrackModel().unlove(track, new SimpleCallback<Object>() {
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
                    if (PreferencesManager.getPreferences().getBoolean("shake_next", false)) {
                        initShakeDetector();
                    } else {
                        destroyShake();
                    }
                    break;
            }
        }

        return START_STICKY;
    }

    private void releaseLocks() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        if (wifiLock.isHeld()) {
            wifiLock.release();
        }
    }

    private void acquireLocks() {
        wakeLock.acquire();
        wifiLock.acquire();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int buffered) {
        LBApplication.bus.post(new BufferizationEvent(buffered));
        this.currentBuffer = buffered;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        next();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        currentPositionPercent = 0;
        if (Timeline.getInstance().getPlaylistTracks().size() <= 0) {
            return;
        }
        prepared = true;
        LBApplication.bus.post(new PreparedEvent());
        Track currentTrack = Timeline.getInstance().getCurrentTrack();
        currentTrack.setDuration(mediaPlayer.getDuration());
        hasDataSource = true;
        scrobbled = false;
        if (Utils.isOnline()) {
            if (!currentTrack.getArtist()
                    .equals(Timeline.getInstance().getPreviousArtist())) {
                getArtistInfo(currentTrack.getArtist(),
                        AuthorizationInfoManager.getLastfmName());
            }
            getTrackInfo(currentTrack);
        }
        saveTrackState();
        if (playOnPrepared) {
            mediaPlayer.start();
            startUpdaters();
            LBApplication.bus.post(new PlayEvent());
            startPlayProgressUpdater();
            showTrackInNotification();
        } else {
            LBApplication.bus.post(new PlayWithoutIconEvent());
//            if (!playedOnce) {
//                mediaPlayer.seekTo(Timeline.getInstance());
            LBApplication.bus.post(new UpdatePositionEvent());
//            }
//            playedOnce = true;
        }
        int result = manager.requestAudioFocus(focusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            if (Timeline.getInstance().getPlayingStateBeforeCall() == PlayingState.PLAYING) {
                play();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                CompatIcs.registerRemote(this, manager);
                CompatIcs.updateRemote(MusicService.this, currentTrack);
            } else {
                MediaButtonReceiver.registerMediaButton(this);
            }
        }
    }

    public int getCurrentBuffer() {
        return currentBuffer;
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

    public void pause() {
        Timeline.getInstance().setPlayingState(PlayingState.DEFAULT);
        releaseLocks();
        if (hasDataSource) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            showTrackInNotification();
            LBApplication.bus.post(new PauseEvent());
        }
        stopUpdaters();
    }

    private void play() {
        Timeline.getInstance().setPlayingState(PlayingState.PLAYING);
        acquireLocks();
        if (hasDataSource) {
            mediaPlayer.start();
            showTrackInNotification();
            startUpdaters();
            playOnPrepared = true;
            startPlayProgressUpdater();
            LBApplication.bus.post(new PlayEvent());
        }
    }

    public void play(int index) {
        Timeline.getInstance().setIndex(index);
        urlNumber = 0;
        updateWidgets();

//        Timeline.getInstance().addToPrevClicked(Timeline.getInstance().getIndex());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            CompatIcs.registerRemote(MusicService.this, manager);
            if (Timeline.getInstance().getCurrentTrack() != null) {
                CompatIcs.updateRemote(MusicService.this,
                        Timeline.getInstance().getCurrentTrack());
            }
        } else {
            MediaButtonReceiver.registerMediaButton(MusicService.this);
        }
        Timeline.getInstance().setPlaylistChanged(false);
        final Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (currentTrack == null) return;
        if (true) { //add
            Timeline.getInstance().addToPrevIndexes(Timeline.getInstance().getIndex());
//            Timeline.getInstance().getPlaylistTracks().get(Timeline.getInstance().getPreviousTracksIndexes().peek())
//                    .setCurrent(false);
        }
        acquireLocks();
        if (currentTrack.getUrl() != null && !currentTrack.getUrl().isEmpty()) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
            } catch (Exception ignored) {
            }
            try {
                mediaPlayer.setDataSource(currentTrack.getUrl());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } else {
            final SharedPreferences urlNumberPreferences =
                    PreferencesManager.getUrlNumberPreferences();
            if (!urlChanged) {
                urlNumber = urlNumberPreferences.getInt(TrackUtils.getNotation(currentTrack), 0);
            } else {
                urlChanged = false;
            }
            getTrackUrl(currentTrack, true, urlNumber);
        }
        if (playOnPrepared) {
            showNotificationToast();
            showTrackInNotification();
            LBApplication.bus.post(new PlayEvent());
        } else {
            LBApplication.bus.post(new PlayWithoutIconEvent());
        }
        LBApplication.bus.post(new TrackInfoEvent());
    }

    private void playWithUrl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            CompatIcs.registerRemote(MusicService.this, manager);
            if (Timeline.getInstance().getCurrentTrack() != null) {
                CompatIcs.updateRemote(MusicService.this,
                        Timeline.getInstance().getCurrentTrack());
            }
        } else {
            MediaButtonReceiver.registerMediaButton(MusicService.this);
        }
        Timeline.getInstance().setPlaylistChanged(true);
        final Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (currentTrack == null) return;
        if (true) {
            Timeline.getInstance().addToPrevIndexes(Timeline.getInstance().getIndex());
//            Timeline.getInstance().getPlaylistTracks().get(Timeline.getInstance().getPreviousTracksIndexes().peek()).setCurrent(false);
        }
        if (currentTrack.getUrl() != null && !currentTrack.getUrl().isEmpty()) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
            } catch (Exception ignored) {
            }
            acquireLocks();
            try {
                mediaPlayer.setDataSource(currentTrack.getUrl());
                mediaPlayer.prepareAsync();
            } catch (IOException | IllegalStateException e) {
                e.printStackTrace();
            }
        }
        if (playOnPrepared) {
            showNotificationToast();
            showTrackInNotification();
            LBApplication.bus.post(new PlayEvent());
        } else {
            LBApplication.bus.post(new PlayWithoutIconEvent());
        }
        LBApplication.bus.post(new TrackInfoEvent());
    }

    private void togglePlayPause() {
        if (Timeline.getInstance().getPlayingState() == PlayingState.PLAYING) {
            pause();
        } else {
            play();
        }
    }

    public void prev() {

    }

    public void next() {

    }

    public void seekTo(int position) {

    }

    private void exit() {
        LBApplication.bus.post(new ExitEvent());
        stopSelf();
    }

    public int getDuration() {
        if (mediaPlayer == null) return 0;
        int duration;
        try {
            duration = mediaPlayer.getDuration();
        } catch (IllegalStateException e) {
            duration = 0;
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

    public void showTrackInNotification() {
        Track track = Timeline.getInstance().getCurrentTrack();
        Notification notification = new TrackNotification().create(this, track);
        bluetoothNotifyChange(AVRCP_META_CHANGED, track);
        startForeground(LIQUID_BEAR_ID, notification);
    }

    private void showNotificationToast() {
        if (PreferencesManager.getPreferences()
                .getBoolean(Constants.SHOW_TOAST_TRACK_CHANGE, false)) {
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
                if (PreferencesManager.getPreferences().getString("timer_action", "1").equals("1")) {
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

    private void startNowplayingUpdater() {
        if (nowplayingSubscriber != null && !nowplayingSubscriber.isUnsubscribed()) {
            nowplayingSubscriber.unsubscribe();
        }
        if (Utils.isOnline() && AuthorizationInfoManager.isAuthorizedOnLastfm()
                && PreferencesManager.getPreferences().getBoolean("nowplaying_check_box_preferences", true)
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
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            updateNowPlaying(Timeline.getInstance().getCurrentTrack());
                        }
                    });
        }
    }

    private void updateNowPlaying(final Track currentTrack) {
        if (PreferencesManager.getPreferences().getBoolean("nowplaying_check_box_preferences", true)) {
            new LastfmTrackModel().nowplaying(currentTrack, new PassiveCallback());
        }
        if (PreferencesManager.getPreferences().getBoolean("nowplaying_vk_check_box_preferences", true)) {
            new VkStatusModel().updateStatus(currentTrack, new VkPassiveCallback());
        }
    }

    public void nextUrl(int urlNumber) {
        this.urlNumber = urlNumber;
        urlChanged = true;
        final SharedPreferences urlNumberPreferences = PreferencesManager.getUrlNumberPreferences();
        final SharedPreferences.Editor editor = urlNumberPreferences.edit();
        final Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (currentTrack == null) return;
        final String notation = TrackUtils.getNotation(currentTrack);
        editor.putInt(notation, urlNumber);
        editor.apply();
        currentTrack.setUrl(null);
        play();
    }

    public void setPlayOnPrepared(boolean playOnPrepared) {
        this.playOnPrepared = playOnPrepared;
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
                LBApplication.bus.post(new TimeEvent());
            }
        };
        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(playProgressSubscriber);
    }

    public void stopPlayProgressUpdater() {
        if (playProgressSubscriber != null && !playProgressSubscriber.isUnsubscribed()) {
            playProgressSubscriber.unsubscribe();
        }
    }

    public boolean isPrepared() {
        return prepared;
    }

    private void getTrackUrl(final Track trackToFind, final boolean current, final int urlNumber) {
//        QueryManager.setTrackUrlQuery(null);
        final Timeline timeline = Timeline.getInstance();
        if (trackToFind.isLocal()) {
            if (timeline.getCurrentTrack() != null) {
                timeline.getCurrentTrack().setUrl(trackToFind.getUrl());
                if (!current) {
                    playOnPrepared = false;
                }
                play();
            } else {
                next();
            }
            return;
        }
        if (!Utils.isOnline()) {
            LBApplication.bus.post(new NetworkStateChangeEvent());
            return;
        }
        new VkAudioModel().getTrackByNotation(trackToFind, urlNumber, new VkSimpleCallback<VkTrack>() {
            @Override
            public void success(VkTrack track) {
                if (!Utils.isOnline()) {
//                    QueryManager.setTrackUrlQuery(new TrackUrlQuery(trackToFind, current,
//                            urlNumber, this, System.currentTimeMillis()));
                }
                if (track != null) {
                    Track currentTrack = timeline.getCurrentTrack();
                    if (currentTrack != null) {
                        currentTrack.setUrl(track.getUrl());
                        currentTrack.setAudioId(track.getAudioId());
                        currentTrack.setOwnerId(track.getOwnerId());
                        if (!current) {
                            playOnPrepared = false;
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
        });
    }

    private void getTrackInfo(final Track track) {
        new LastfmTrackModel().getTrackInfo(track, AuthorizationInfoManager.getLastfmName(),
                new SimpleCallback<LastfmTrack>() {
                    @Override
                    public void success(LastfmTrack lastfmTrack) {
                        Album album = Converter.convertAlbum(lastfmTrack.getAlbum());
                        Boolean loved = lastfmTrack.isLoved();
                        Intent intent = new Intent();
                        intent.setAction(Constants.ACTION_SERVICE);
                        LBApplication.bus.post(new AlbumInfoEvent());
                        Timeline.getInstance().setCurrentAlbum(album);
                        new LastfmAlbumModel().getCover(album, new CompletionCallback() {
                            @Override
                            public void onCompleted() {
                                showTrackInNotification();
                            }
                        });
                        track.setLoved(loved);
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

    private void getArtistInfo(final String artist, final String username) {
        new LastfmArtistModel().getArtistInfo(artist, username, new SimpleCallback<LastfmArtist>() {
            @Override
            public void success(LastfmArtist lastfmArtist) {
                Timeline.getInstance().setPreviousArtist(artist);
                List<LastfmImage> list = lastfmArtist.getImages();
                String imageUrl = null;
                if (list.size() != 0) {
                    LastfmImage lastfmImage = list.get(list.size() - 1);
                    imageUrl = lastfmImage != null ? lastfmImage.getUrl() : null;
                }
                LBApplication.bus.post(new ArtistInfoEvent(imageUrl));
            }

            @Override
            public void failure(String error) {
            }
        });
    }

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}

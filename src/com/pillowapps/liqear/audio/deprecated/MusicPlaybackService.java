package com.pillowapps.liqear.audio.deprecated;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.activities.SearchActivity;
import com.pillowapps.liqear.audio.EqualizerManager;
import com.pillowapps.liqear.audio.HeadsetStateReceiver;
import com.pillowapps.liqear.audio.MediaButtonReceiver;
import com.pillowapps.liqear.entities.RepeatMode;
import com.pillowapps.liqear.entities.ShuffleMode;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Track;
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
import com.pillowapps.liqear.helpers.Utils;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class MusicPlaybackService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener {
    public static final int TRACK_INFO_CALLBACK = 0;
    public static final int PRAPARED_CALLBACK = 1;
    public static final int BUFFERIZATION_CALLBACK = 2;
    public static final int PAUSE_CALLBACK = 3;
    public static final int PLAY_CALLBACK = 4;
    public static final int PLAY_WITHOUT_ICON_CALLBACK = 5;
    public static final int UPDATE_POSITION_CALLBACK = 6;
    public static final int EXIT_CALLBACK = 7;
    public static final int SHOW_PROGRESS_BAR = 8;
    public static final int ARTIST_INFO_CALLBACK = 9;
    public static final int NO_URL_CALLBACK = 10;
    public static final int INTERNET_STATE_CHANGE = 11;
    public static final int ALBUM_INFO_CALLBACK = 12;
    public static final int TIME_CALLBACK = 13;
    public static final String ACTION_TOGGLE_PLAYBACK_NOTIFICATION =
            "com.pillowapps.liqear.TOGGLE_PLAYBACK";
    public static final String ACTION_PREV = "com.pillowapps.liqear.PREV";
    public static final String ACTION_NEXT = "com.pillowapps.liqear.NEXT";
    public static final String ACTION_CLOSE = "com.pillowapps.liqear.FORCE_CLOSE";
    public static final String ACTION_SHUFFLE = "com.pillowapps.liqear.SHUFFLE";
    public static final String ACTION_REPEAT = "com.pillowapps.liqear.REPEAT";
    public static final String ACTION_LOVE = "com.pillowapps.liqear.LOVE";
    public static final String ACTION_ADD_TO_VK = "com.pillowapps.liqear.ADD_TO_VK";
    public static final String ACTION_ADD_TO_VK_FAST = "com.pillowapps.liqear.ADD_TO_VK_FAST";
    public static final String CHANGE_SHAKE_PREFERENCE = "com.pillowapps.liqear.CHANGE_SHAKE_PREFERENCE";
    private static final int NOTIFICATION_ID = 1337;
    private static final String AVRCP_PLAYSTATE_CHANGED = "com.android.music.playstatechanged";
    private static final String AVRCP_META_CHANGED = "com.android.music.metachanged";
    private final IBinder binder = new LocalBinder();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean playOnPrepared = false;
    private boolean hasDataSource = false;
    private boolean scrobbled = false;
    private boolean playedOnce = false;
    private int currentPositionPercent = 0;
    private int urlNumber = 0;
    private boolean urlChanged = false;
    private Handler nowplayingHandler = new Handler();
    private Handler percentHandler = new Handler();
    private Handler timerHandler = new Handler();
    private HeadsetStateReceiver headsetReceiver;
    private AudioManager manager;
    private PhoneStateListener phoneStateListener;
    private boolean prepared = false;
    private int seconds;
    private Handler toastHandler = new Handler();
    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                            || focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        if (AudioTimeline.isStateActive()) {
                            pause(true);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                CompatIcs.unregisterRemote(MusicPlaybackService.this, manager);
                            } else {
                                MediaButtonReceiver.unregisterMediaButton(MusicPlaybackService.this);
                            }
                        }
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        if (AudioTimeline.wasPlayingBeforeCall()) {
                            play();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            CompatIcs.registerRemote(MusicPlaybackService.this, manager);
                            if (AudioTimeline.getCurrentTrack() != null) {
                                CompatIcs.updateRemote(MusicPlaybackService.this,
                                        AudioTimeline.getCurrentTrack());
                            }
                        } else {
                            MediaButtonReceiver.registerMediaButton(MusicPlaybackService.this);
                        }
                    }
                }
            };
    private Handler playHandler = new Handler();
    private Thread thread;
    private int currentBuffer = 0;
    private ConnectionChangeReceiver connectionReceiver;
    private WakeLock wakeLock;
    private WifiLock wifiLock;
    private Handler handler;
    private SensorManager mSensorManager;
    private ShakeDetector mShakeDetector;

    @Override
    public void onCreate() {
        super.onCreate();
//        QueryManager.getInstance().scrobbleOffline();
        AudioTimeline.setMusicPlaybackService(this);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        PowerManager pm = (PowerManager) getApplicationContext()
                .getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Liqear");
        WifiManager wifimanager = (WifiManager) LBApplication.getAppContext()
                .getSystemService(Context.WIFI_SERVICE);
        wifiLock = wifimanager.createWifiLock("player_on");
        manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        final Track currentTrack = AudioTimeline.getCurrentTrack();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            CompatIcs.registerRemote(this, manager);
            if (currentTrack != null) {
                CompatIcs.updateRemote(MusicPlaybackService.this, currentTrack);
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
        mediaPlayer.setAudioSessionId(1337);
        manager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        afterRestoreState();
        initWidgets();
        connectionReceiver = new ConnectionChangeReceiver();
        registerReceiver(connectionReceiver, new IntentFilter(Constants.NETWORK_ACTION));
        if (PreferencesManager.getPreferences().getBoolean("shake_next", false)) {
            initShakeDetector();
        }
    }

    private void initShakeDetector() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                next();
            }
        });
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI);
    }

    private void initWidgets() {
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        FourWidthOneHeightWidget.checkEnabled(this, manager);
        FourWidthThreeHeightWidget.checkEnabled(this, manager);
        FourWidthThreeHeightAltWidget.checkEnabled(this, manager);
    }

    public void updateWidgets() {
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        FourWidthOneHeightWidget.updateWidget(this, manager, AudioTimeline.isPlaying());
        FourWidthThreeHeightWidget.updateWidget(this, manager, AudioTimeline.isPlaying());
        FourWidthThreeHeightAltWidget.updateWidget(this, manager, AudioTimeline.isPlaying());
    }

    /**
     * Calls when startService.
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) return START_STICKY;
            switch (action) {
                case ACTION_TOGGLE_PLAYBACK_NOTIFICATION:
                    setPlayOnPrepared(true);
                    if (hasDataSource) {
                        playPause();
                    } else {
                        AudioTimeline.setPlaying(true);
                        List<Track> tracks = PlaylistManager.getInstance().loadPlaylist();
                        AudioTimeline.setPlaylist(tracks);
                        tracks = AudioTimeline.getPlaylist();
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
                        AudioTimeline.setCurrentIndex(currentIndex);
                        if (currentIndex > AudioTimeline.getPlaylist().size()) {
                            position = 0;
                        }
                        if (!PreferencesManager.getPreferences()
                                .getBoolean("continue_from_position", true)) {
                            position = 0;
                        }

                        AudioTimeline.setCurrentPosition(position);
                        if (AudioTimeline.getPlaylist().size() != 0
                                && AudioTimeline.hasCurrentTrack()) {
                            play(true);
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
                    AudioTimeline.toggleShuffle();
                    updateWidgets();
                    break;
                case ACTION_REPEAT:
                    AudioTimeline.toggleRepeat();
                    updateWidgets();
                    break;
                case ACTION_ADD_TO_VK: {
                    Track track = AudioTimeline.getCurrentTrack();
                    if (track != null) {
                        Intent searchVkIntent = new Intent(getBaseContext(),
                                SearchActivity.class);
                        searchVkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        searchVkIntent.putExtra(SearchActivity.SEARCH_MODE,
                                SearchActivity.SearchMode.AUDIO_SEARCH_RESULT_ADD_VK);
                        searchVkIntent.putExtra(Constants.TARGET, track.getNotation());
                        getApplication().startActivity(searchVkIntent);
                    }
                    break;
                }
                case ACTION_ADD_TO_VK_FAST: {
                    final Track track = AudioTimeline.getCurrentTrack();
                    if (track != null) {
                        new VkAudioModel().addToUserAudioFast(track.getNotation(), new VkSimpleCallback<VkResponse>() {
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
                    final Track track = AudioTimeline.getCurrentTrack();
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

    private void exit() {
        sendCallback(EXIT_CALLBACK);
        stopSelf();
    }

    private void startUpdaters() {
        stopUpdating();
        startNowPlayingUpdater();
        startPercentUpdater();
    }

    private void stopUpdating() {
        percentHandler.removeCallbacksAndMessages(null);
        nowplayingHandler.removeCallbacksAndMessages(null);
    }

    private void startPercentUpdater() {
        if (AuthorizationInfoManager.isAuthorizedOnLastfm()
                && AudioTimeline.isPlaying() && !scrobbled) {
            if (PreferencesManager.getPreferences()
                    .getBoolean("scrobble_check_box_preferences", true)) {
                currentPositionPercent++;
                int needToScrobble = PreferencesManager.getPreferences()
                        .getInt("scrobble_time_preferences", 40);
                if ((currentPositionPercent > needToScrobble - 2
                        && currentPositionPercent < needToScrobble + 2)
                        || getCurrentPosition() / 1000 > 240) {
                    Track track = AudioTimeline.getCurrentTrack();
                    if (track != null) {
                        scrobble(track.getArtist(), track.getTitle(), track.getAlbum(), Utils.getCurrentTime());
                    }
                    scrobbled = true;
                }
            }
            Runnable notification = new Runnable() {
                public void run() {
                    startPercentUpdater();
                }
            };
            percentHandler.postDelayed(notification, getDuration() / 100);
        }
    }

    @Override
    public void onDestroy() {
        AudioTimeline.setPlaying(false);
        updateWidgets();
        destroy();
        manager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        super.onDestroy();
    }

    private void destroy() {
        releaseLocks();
        destroyShake();
        unregisterReceiver(connectionReceiver);
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
            CompatIcs.unregisterRemote(MusicPlaybackService.this, manager);
        } else {
            MediaButtonReceiver.unregisterMediaButton(this);
        }
        EqualizerManager.releaseEqualizer();
        unregisterPhoneCallReceiver();
    }

    private void destroyShake() {
        try {
            mSensorManager.unregisterListener(mShakeDetector);
        } catch (Exception ignored) {
        }
    }

    private void releaseLocks() {
        try {
            wakeLock.release();
        } catch (Exception ignored) {
        }
        try {
            wifiLock.release();
        } catch (Exception ignored) {
        }
    }

    private void acquireLocks() {
        try {
            wakeLock.acquire();
        } catch (Exception ignored) {
        }
        try {
            wifiLock.acquire();
        } catch (Exception ignored) {
        }
    }

    private void saveState() {
        saveTrackState();
        SharedPreferences.Editor editor = PreferencesManager.getPreferences().edit();
        editor.putInt(Constants.CURRENT_POSITION, getCurrentPosition());
        editor.putBoolean(Constants.SHUFFLE_MODE_ON,
                AudioTimeline.getShuffleMode() == ShuffleMode.SHUFFLE);
        editor.putBoolean(Constants.REPEAT_MODE_ON,
                AudioTimeline.getRepeatMode() == RepeatMode.REPEAT);
        editor.apply();
    }

    private void saveTrackState() {
        SharedPreferences.Editor editor = PreferencesManager.getPreferences().edit();
        final Track currentTrack = AudioTimeline.getCurrentTrack();
        if (AudioTimeline.getPlaylist() != null
                && AudioTimeline.getPlaylist().size() != 0
                && currentTrack != null) {
            editor.putString(Constants.ARTIST, currentTrack.getArtist());
            editor.putString(Constants.TITLE, currentTrack.getTitle());
            editor.putInt(Constants.DURATION, currentTrack
                    .getDuration());
        }
        editor.putInt(Constants.CURRENT_INDEX, AudioTimeline.getCurrentIndex());
        editor.apply();
    }

    /**
     * Every 15 seconds updates lastfm nowplaying status
     */
    private void startNowPlayingUpdater() {
        if (Utils.isOnline() && AuthorizationInfoManager.isAuthorizedOnLastfm()) {
            try {
                if (PreferencesManager.getPreferences()
                        .getBoolean("nowplaying_check_box_preferences", true)
                        && AudioTimeline.getPlaylist().size() > AudioTimeline
                        .getCurrentIndex()) {
                    updateNowPlaying(AudioTimeline.getCurrentTrack());
                }
            } catch (Exception e) {
                // No operations.
            }
            if (AudioTimeline.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        startNowPlayingUpdater();
                    }
                };
                nowplayingHandler.postDelayed(notification, 15000);
            }
        }
    }

    private void startTimerUpdater() {
        if (--seconds <= 0) {
            if (PreferencesManager.getPreferences().getString("timer_action", "1").equals("1")) {
                pause(true);
            } else {
                exit();
            }
            return;
        }
        Runnable notification = new Runnable() {
            public void run() {
                startTimerUpdater();
            }
        };
        timerHandler.postDelayed(notification, 1000);
    }

    public void play(int position) {
        AudioTimeline.setCurrentIndex(position);
        AudioTimeline.setPlaying(true);
        urlNumber = 0;
        play(true);
    }

    private void playThread(final boolean add) {
        if (thread != null) {
            thread.interrupt();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        handler = new Handler(Looper.getMainLooper());
        AudioTimeline.addToPrevClicked(AudioTimeline.getCurrentIndex());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    CompatIcs.registerRemote(MusicPlaybackService.this, manager);
                    if (AudioTimeline.getCurrentTrack() != null) {
                        CompatIcs.updateRemote(MusicPlaybackService.this,
                                AudioTimeline.getCurrentTrack());
                    }
                } else {
                    MediaButtonReceiver.registerMediaButton(MusicPlaybackService.this);
                }
                AudioTimeline.setStillLastPlaylist(false);
                final Track currentTrack = AudioTimeline.getCurrentTrack();
                if (currentTrack == null) return;
                if (add) {
                    AudioTimeline.addToPrevIndexes(AudioTimeline.getCurrentIndex());
                    AudioTimeline.getPlaylist().get(AudioTimeline.peekPrevIndexes())
                            .setCurrent(false);
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
                        urlNumber = urlNumberPreferences.getInt(currentTrack.getNotation(), 0);
                    } else {
                        urlChanged = false;
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getTrackUrl(currentTrack, true, urlNumber);
                        }
                    });
                }
                if (playOnPrepared) {
                    showNotificationToast();
                    showTrackInNotification();
                    sendCallback(PLAY_CALLBACK);
                } else {
                    sendCallback(PLAY_WITHOUT_ICON_CALLBACK);
                }
                sendCallback(TRACK_INFO_CALLBACK);
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }

    private void playWithUrl(final boolean add) {
        if (thread != null) {
            thread.interrupt();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        handler = new Handler(Looper.getMainLooper());
        AudioTimeline.addToPrevClicked(AudioTimeline.getCurrentIndex());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    CompatIcs.registerRemote(MusicPlaybackService.this, manager);
                    if (AudioTimeline.getCurrentTrack() != null) {
                        CompatIcs.updateRemote(MusicPlaybackService.this,
                                AudioTimeline.getCurrentTrack());
                    }
                } else {
                    MediaButtonReceiver.registerMediaButton(MusicPlaybackService.this);
                }
                AudioTimeline.setStillLastPlaylist(false);
                final Track currentTrack = AudioTimeline.getCurrentTrack();
                if (currentTrack == null) return;
                if (add) {
                    AudioTimeline.addToPrevIndexes(AudioTimeline.getCurrentIndex());
                    AudioTimeline.getPlaylist().get(AudioTimeline.peekPrevIndexes())
                            .setCurrent(false);
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
                if (playOnPrepared) {
                    showNotificationToast();
                    showTrackInNotification();
                    sendCallback(PLAY_CALLBACK);
                } else {
                    sendCallback(PLAY_WITHOUT_ICON_CALLBACK);
                }
                sendCallback(TRACK_INFO_CALLBACK);
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }

    private void play(boolean add) {
        updateWidgets();
        playThread(add);
    }

    private void bluetoothNotifyChange(String what) {
        Intent i = new Intent(what);
        Track currentTrack = AudioTimeline.getCurrentTrack();
        int duration = getDuration();
        if (currentTrack != null && duration != 0) {
            i.putExtra("PLAYER_PACKAGE_NAME", "com.pillowapps.liqear");
            i.putExtra("artist", currentTrack.getArtist());
            i.putExtra("track", currentTrack.getTitle());
            i.putExtra("duration", duration);
            i.putExtra("playing", AudioTimeline.isPlaying());
            sendBroadcast(i);
        }
    }

    public void showTrackInNotification() {
        updateWidgets();
        if (AudioTimeline.getCurrentIndex() >= AudioTimeline.getPlaylist().size()) return;
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            notification = createControllingNotification();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                CompatIcs.updateRemote(this, AudioTimeline.getCurrentTrack());
            }
        } else {
            notification = createSimpleNotification();
        }
        bluetoothNotifyChange(AVRCP_META_CHANGED);
        startForeground(NOTIFICATION_ID, notification);
    }

    private void showNotificationToast() {
        if (PreferencesManager.getPreferences()
                .getBoolean(Constants.SHOW_TOAST_TRACK_CHANGE, false)) {
            toastHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MusicPlaybackService.this,
                            Html.fromHtml(AudioTimeline.getCurrentTrack().getNotation()),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public Notification createControllingNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_liquid_bear_logotype_revision)
                        .setTicker(Html.fromHtml(AudioTimeline.getCurrentTrack().getNotation()));
        final RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setTextViewText(R.id.title,
                Html.fromHtml(AudioTimeline.getCurrentTrack().getTitle()));
        contentView.setTextViewText(R.id.artist,
                Html.fromHtml(AudioTimeline.getCurrentTrack().getArtist()));
        mBuilder.setContent(contentView);
        Intent notificationIntent = new Intent(this, MainActivity.class);

        int playButton = AudioTimeline.isPlaying() ? R.drawable.pause_button :
                R.drawable.play_button;
        contentView.setImageViewResource(R.id.play_pause, playButton);

        ComponentName service = new ComponentName(this, MusicPlaybackService.class);
        Intent playPause = new Intent(ACTION_TOGGLE_PLAYBACK_NOTIFICATION);
        playPause.setComponent(service);
        contentView.setOnClickPendingIntent(R.id.play_pause,
                PendingIntent.getService(this, 0, playPause, 0));

        Intent nextIntent = new Intent(ACTION_NEXT);
        nextIntent.setComponent(service);
        contentView.setOnClickPendingIntent(R.id.next,
                PendingIntent.getService(this, 0, nextIntent, 0));

        Intent prevIntent = new Intent(ACTION_PREV);
        prevIntent.setComponent(service);
        contentView.setOnClickPendingIntent(R.id.prev,
                PendingIntent.getService(this, 0, prevIntent, 0));

        Intent closeIntent = new Intent(ACTION_CLOSE);
        closeIntent.setComponent(service);
        contentView.setOnClickPendingIntent(R.id.force_close,
                PendingIntent.getService(this, 0, closeIntent, 0));

        mBuilder.setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, 0));

        Bitmap bitmap = AudioTimeline.getCurrentAlbumBitmap();
        if (bitmap == null) {
            contentView.setInt(R.id.album_cover_image_view, "setImageResource", R.drawable.lb_icon_white);
        } else {
            contentView.setImageViewBitmap(R.id.album_cover_image_view, bitmap);
        }

        mBuilder.setOngoing(true);
        mBuilder.setOnlyAlertOnce(true);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            final RemoteViews bigView = new RemoteViews(getPackageName(), R.layout.notification_big);
            bigView.setTextViewText(R.id.title,
                    Html.fromHtml(AudioTimeline.getCurrentTrack().getTitle()));
            bigView.setTextViewText(R.id.artist,
                    Html.fromHtml(AudioTimeline.getCurrentTrack().getArtist()));

            bigView.setImageViewResource(R.id.play_pause, playButton);

            playPause.setComponent(service);
            bigView.setOnClickPendingIntent(R.id.play_pause,
                    PendingIntent.getService(this, 0, playPause, 0));

            nextIntent.setComponent(service);
            bigView.setOnClickPendingIntent(R.id.next,
                    PendingIntent.getService(this, 0, nextIntent, 0));

            prevIntent.setComponent(service);
            bigView.setOnClickPendingIntent(R.id.prev,
                    PendingIntent.getService(this, 0, prevIntent, 0));

            closeIntent.setComponent(service);
            bigView.setOnClickPendingIntent(R.id.force_close,
                    PendingIntent.getService(this, 0, closeIntent, 0));

            Intent loveIntent = new Intent(ACTION_LOVE);
            loveIntent.setComponent(service);
            int loveButton = Utils.getLoveButtonImage();
            bigView.setInt(R.id.love_button, "setImageResource", loveButton);
            bigView.setOnClickPendingIntent(R.id.love_button,
                    PendingIntent.getService(this, 0, loveIntent, 0));

            Track track = AudioTimeline.getCurrentTrack();
            if (track != null) {
                Intent addToVkFastIntent = new Intent(ACTION_ADD_TO_VK_FAST);
                addToVkFastIntent.setComponent(service);
                if (track.isAddedToVk()) {
                    bigView.setInt(R.id.add_to_vk_button, "setImageResource",
                            R.drawable.add_clicked);
                    bigView.setOnClickPendingIntent(R.id.add_to_vk_button, null);
                } else {
                    bigView.setInt(R.id.add_to_vk_button, "setImageResource",
                            R.drawable.add_button);
                    bigView.setOnClickPendingIntent(R.id.add_to_vk_button,
                            PendingIntent.getService(this, 0, addToVkFastIntent, 0));
                }
            }

            if (bitmap == null) {
                bigView.setInt(R.id.album_cover_image_view, "setImageResource", R.drawable.lb_icon_white);
            } else {
                bigView.setImageViewBitmap(R.id.album_cover_image_view, bitmap);
            }

            mBuilder.setStyle(new NotificationCompat.BigTextStyle());
            notification = mBuilder.build();
            notification.bigContentView = bigView;
        } else {
            notification = mBuilder.build();
        }
        return notification;
    }

    @SuppressWarnings("deprecation")
    public Notification createSimpleNotification() {
        Notification notification = new Notification();
        Track track = AudioTimeline.getCurrentTrack();
        notification.tickerText = Html.fromHtml(track.getNotation());
        notification.icon = R.drawable.ic_stat_liquid_bear_logotype_revision;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        Intent intent = new Intent(MusicPlaybackService.this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this.getBaseContext(), 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setLatestEventInfo(getApplicationContext(), Html.fromHtml(track.getArtist()),
                Html.fromHtml(track.getTitle()), pi);
        return notification;
    }

    /**
     * Toggles play/pause
     */
    public void playPause() {
        if (AudioTimeline.isPlaying()) {
            pause(true);
        } else {
            play();
        }
    }

    public void play() {
        AudioTimeline.setPlaying(true);
        acquireLocks();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (hasDataSource) {
                    mediaPlayer.start();
                    showTrackInNotification();
                    startUpdaters();
                    playOnPrepared = true;
                    startPlayProgressUpdater();
                    sendCallback(PLAY_CALLBACK);
                }
            }
        }).start();
    }

    public void pause(final boolean savePlayingState) {
        AudioTimeline.setPlaying(false);
        releaseLocks();
        if (hasDataSource) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            } catch (IllegalStateException ignored) {
            }
            showTrackInNotification();
            stopUpdating();
            sendCallback(PAUSE_CALLBACK);
            stopPlayProgressUpdater();
        }
        if (savePlayingState) AudioTimeline.savePlayingState();
    }

    private void sendCallback(int callback) {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_SERVICE);
        intent.putExtra(Constants.CALLBACK_TYPE, callback);
        sendBroadcast(intent);
        switch (callback) {
            case PLAY_CALLBACK:
                AudioTimeline.setStateActive(true);
                break;
            case PAUSE_CALLBACK:
                AudioTimeline.setStateActive(false);
                break;
            default:
                break;
        }
    }

    /**
     * Calls when track in MediaPlayer ends
     */
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    public void setPlayOnPrepared(boolean playOnPrepared) {
        this.playOnPrepared = playOnPrepared;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
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

    /**
     * Calls when player prepared asynchronously
     */
    public void onPrepared(MediaPlayer mp) {
        currentPositionPercent = 0;
        if (AudioTimeline.getPlaylist().size() <= 0) {
            return;
        }
        prepared = true;
        sendCallback(PRAPARED_CALLBACK);
        AudioTimeline.getCurrentTrack().setDuration(mp.getDuration());
        hasDataSource = true;
        scrobbled = false;
        if (Utils.isOnline()) {
            if (!AudioTimeline.getCurrentTrack().getArtist()
                    .equals(AudioTimeline.getPreviousArtist())) {
                getArtistInfo(AudioTimeline.getCurrentTrack().getArtist(),
                        AuthorizationInfoManager.getLastfmName());
            }
            getTrackInfo(AudioTimeline.getCurrentTrack());
        }
        saveTrackState();
        if (playOnPrepared) {
            mp.start();
            startUpdaters();
            sendCallback(PLAY_CALLBACK);
            startPlayProgressUpdater();
            showTrackInNotification();
        } else {
            sendCallback(PLAY_WITHOUT_ICON_CALLBACK);
            if (!playedOnce) {
                mp.seekTo(AudioTimeline.getCurrentPosition());
                sendCallback(UPDATE_POSITION_CALLBACK);
            }
            playedOnce = true;
        }
        int result = manager.requestAudioFocus(focusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            if (AudioTimeline.wasPlayingBeforeCall()) {
                play();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                CompatIcs.registerRemote(this, manager);
                if (AudioTimeline.getCurrentTrack() != null) {
                    CompatIcs.updateRemote(MusicPlaybackService.this,
                            AudioTimeline.getCurrentTrack());
                }
            } else {
                MediaButtonReceiver.registerMediaButton(this);
            }
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        return true;
    }

    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (percent == 0) {
            return;
        }
        currentBuffer = percent;
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_SERVICE);
        intent.putExtra(Constants.CALLBACK_TYPE, BUFFERIZATION_CALLBACK);
        intent.putExtra("track-buffering", percent);
        sendBroadcast(intent);
    }

    public void seekTo(int position) {
        if (position > mediaPlayer.getDuration()) {
            position = 0;
        }
        mediaPlayer.seekTo(position);
        AudioTimeline.setCurrentPosition(position);
    }

    public void prev() {
        startChangeTrack(false);
    }

    public int getCurrentBuffer() {
        return currentBuffer;
    }

    private void startChangeTrack(boolean next) {
        percentHandler.removeCallbacksAndMessages(null);
        Runnable runnable;
        PreferencesManager.getPreferences().edit().putInt(Constants.CURRENT_POSITION, 0).commit();
        if (next) {
            runnable = new Runnable() {
                public void run() {
                    List<Track> playlist = AudioTimeline.getPlaylist();
                    if (playlist == null) return;
                    if (AudioTimeline.getWithoutUrls() >= playlist.size()) {
                        sendCallback(NO_URL_CALLBACK);
                        return;
                    }
                    switch (AudioTimeline.getRepeatMode()) {
                        case REPEAT_PLAYLIST:
                            int tracklistSize = playlist.size();
                            if (tracklistSize == 0) {
                                return;
                            }
                            int currentIndex = AudioTimeline.getCurrentIndex();
                            switch (AudioTimeline.getShuffleMode()) {
                                case DEFAULT:
                                    currentIndex = (currentIndex + 1) % tracklistSize;
                                    break;
                                case SHUFFLE:
                                    currentIndex = AudioTimeline.calculateRandomIndex();
                                    break;
                                default:
                                    break;
                            }
                            LinkedList<Integer> queue = AudioTimeline.getQueue();
                            if (queue != null && !queue.isEmpty()) {
                                currentIndex = queue.poll();
                            }
                            AudioTimeline.setCurrentIndex(currentIndex);
                            updateWidgets();
                            break;
                        default:
                            break;
                    }
                    play(true);
                }
            };
        } else {
            runnable = new Runnable() {
                @Override
                public void run() {
                    Stack<Integer> prevTracksIndexes = AudioTimeline.getPrevTracksIndexes();
                    if (prevTracksIndexes.size() == 0) {
                        return;
                    }

                    if (prevTracksIndexes.size() > 1) {
                        prevTracksIndexes.pop();
                    }

                    int prevPosition = prevTracksIndexes.peek();
                    AudioTimeline.setCurrentIndex(prevPosition);
                    updateWidgets();
                    play(false);
                }
            };
        }
        percentHandler.post(runnable);
    }

    public void next() {
        startChangeTrack(true);
    }

    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Restoring service state after application clear start
     */
    private void afterRestoreState() {
        try {
            EqualizerManager.restoreEqualizer();
            EqualizerManager.setEnabled(PreferencesManager.getEqualizerPreferences()
                    .getBoolean("enabled", true));
        } catch (Exception ignored) {
        }
        final SharedPreferences urlNumberPreferences = PreferencesManager.getUrlNumberPreferences();
        final Track currentTrack = AudioTimeline.getCurrentTrack();
        if (currentTrack == null) return;
        urlNumber = urlNumberPreferences.getInt(currentTrack.getNotation(), 0);
        if (AudioTimeline.hasSaves()) {
            AudioTimeline.addToPrevIndexes(AudioTimeline.getCurrentIndex());
            AudioTimeline.addToPrevClicked(AudioTimeline.getCurrentIndex());
            getTrackUrl(currentTrack, false, urlNumber);
        }
    }

    public void nextUrl(int urlNumber) {
        this.urlNumber = urlNumber;
        urlChanged = true;
        final SharedPreferences urlNumberPreferences = PreferencesManager.getUrlNumberPreferences();
        final SharedPreferences.Editor editor = urlNumberPreferences.edit();
        final Track currentTrack = AudioTimeline.getCurrentTrack();
        if (currentTrack == null) return;
        final String notation = currentTrack.getNotation();
        editor.putInt(notation, urlNumber);
        editor.apply();
        AudioTimeline.getCurrentTrack().setUrl(null);
        play(false);
    }

    public boolean isPrepared() {
        return prepared;
    }

    public void setTimer(int seconds) {
        if (seconds == 0) {
            timerHandler.removeCallbacksAndMessages(null);
            return;
        }
        this.seconds = seconds;
        startTimerUpdater();
    }

    private void registerPhoneCallReceiver() {
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        pause(true);
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (AudioTimeline.wasPlayingBeforeCall())
                            play();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        pause(true);
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

    private void getTrackUrl(final Track currentTrack, final boolean current, final int urlNumber) {
//        QueryManager.setTrackUrlQuery(null);
        if (currentTrack.isLocal()) {
            if (AudioTimeline.getCurrentTrack() != null) {
                AudioTimeline.getCurrentTrack().setUrl(currentTrack.getUrl());
                if (!current) {
                    playOnPrepared = false;
                }
                play(true);
            } else {
                AudioTimeline.incrementWithoutUrl();
                next();
            }
            return;
        }
        if (!Utils.isOnline()) {
            sendCallback(INTERNET_STATE_CHANGE);
            return;
        }
        new VkAudioModel().getTrackByNotation(currentTrack, urlNumber, new VkSimpleCallback<VkTrack>() {
            @Override
            public void success(VkTrack track) {
                if (!Utils.isOnline()) {
//                    QueryManager.setTrackUrlQuery(new TrackUrlQuery(currentTrack, current,
//                            urlNumber, this, System.currentTimeMillis()));
                }
                if (track != null) {
                    if (AudioTimeline.getCurrentTrack() != null) {
                        AudioTimeline.getCurrentTrack().setUrl(track.getUrl());
                        AudioTimeline.getCurrentTrack().setAid(track.getAudioId());
                        AudioTimeline.getCurrentTrack().setOwnerId(track.getOwnerId());
                        if (!current) {
                            playOnPrepared = false;
                        }
                        playWithUrl(false);
                    } else {
                        AudioTimeline.incrementWithoutUrl();
                        next();
                    }
                } else {
                    AudioTimeline.incrementWithoutUrl();
                    next();
                }
            }

            @Override
            public void failure(VkError error) {
                AudioTimeline.incrementWithoutUrl();
                next();
            }
        });
    }

    private void scrobble(final String artist, final String title, final String album, final String currentTime) {
        if (Utils.isOnline()) {
            new LastfmTrackModel().scrobble(artist, title, album, currentTime, new PassiveCallback());
        } else {
            PlaylistManager.getInstance().addTrackToScrobble(artist, title, currentTime);
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

    private void getArtistInfo(final String artist, final String username) {
        new LastfmArtistModel().getArtistInfo(artist, username, new SimpleCallback<LastfmArtist>() {
            @Override
            public void success(LastfmArtist lastfmArtist) {
                AudioTimeline.setPreviousArtist(artist);
                List<LastfmImage> list = lastfmArtist.getImages();
                String imageUrl = null;
                if (list.size() != 0) {
                    LastfmImage lastfmImage = list.get(list.size() - 1);
                    imageUrl = lastfmImage != null ? lastfmImage.getUrl() : null;
                }
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_SERVICE);
                intent.putExtra(Constants.CALLBACK_TYPE, ARTIST_INFO_CALLBACK);
                intent.putExtra(Constants.IMAGE_URL, imageUrl);
                sendBroadcast(intent);
            }

            @Override
            public void failure(String error) {
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
                        intent.putExtra(Constants.CALLBACK_TYPE, ALBUM_INFO_CALLBACK);
                        AudioTimeline.setCurrentAlbum(album);
                        new LastfmAlbumModel().getCover(album, new CompletionCallback() {
                            @Override
                            public void onCompleted() {
                                showTrackInNotification();
                            }
                        });
                        track.setLoved(loved);
                        intent.putExtra(Constants.TRACK_IS_LOVED, loved);
                        sendBroadcast(intent);
                        updateWidgets();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            CompatIcs.updateRemote(MusicPlaybackService.this,
                                    AudioTimeline.getCurrentTrack());
                        }
                    }

                    @Override
                    public void failure(String errorMessage) {

                    }
                });
    }

    public void stopPlayProgressUpdater() {
        playHandler.removeCallbacksAndMessages(null);
    }

    public void startPlayProgressUpdater() {
        sendCallback(TIME_CALLBACK);
        Runnable notification = new Runnable() {
            public void run() {
                startPlayProgressUpdater();
            }
        };
        playHandler.postDelayed(notification, 1000);
    }

    public class LocalBinder extends Binder {
        public MusicPlaybackService getService() {
            return MusicPlaybackService.this;
        }
    }

    public class ConnectionChangeReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
//            sendCallback(INTERNET_STATE_CHANGE);
//            if (intent.getExtras() != null) {
//                NetworkInfo ni = (NetworkInfo) intent.getExtras()
//                        .get(ConnectivityManager.EXTRA_NETWORK_INFO);
//                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
//                    final TrackUrlQuery trackUrlQuery = QueryManager.getTrackUrlQuery();
//                    if (trackUrlQuery != null
//                            && System.currentTimeMillis() - trackUrlQuery.getTimeStamp() < 3000) {
//                        final Track currentTrack = trackUrlQuery.getCurrentTrack();
//                        final boolean current = trackUrlQuery.isCurrent();
//                        final int trackUrlNumber = trackUrlQuery.getUrlNumber();
//                        if (currentTrack.getUrl() == null || currentTrack.getUrl().isEmpty()) {
//                            urlQueryManager.getTrackByNotation(currentTrack, current, trackUrlNumber,
//                                    new GetResponseCallback() {
//                                        @Override
//                                        public void onDataReceived(ReadyResult result) {
//                                            final boolean error = !result.isOk();
//                                            if (error) {
//                                                AudioTimeline.incrementWithoutUrl();
//                                                next();
//                                                return;
//                                            }
//                                            final Object object = result.getObject();
//                                            if (object != null) {
//                                                List<Object> objects = (List<Object>) object;
//                                                if (objects.size() > 0
//                                                        && AudioTimeline.getCurrentTrack() != null) {
//                                                    AudioTimeline.getCurrentTrack()
//                                                            .setUrl((String) objects.get(0));
//                                                    AudioTimeline.getCurrentTrack()
//                                                            .setAid((Long) objects.get(1));
//                                                    AudioTimeline.getCurrentTrack()
//                                                            .setOwnerId((Long) objects.get(2));
//                                                    if (!current) {
//                                                        playOnPrepared = false;
//                                                    }
//                                                    play(true);
//                                                } else {
//                                                    AudioTimeline.incrementWithoutUrl();
//                                                    next();
//                                                }
//                                            } else {
//                                                AudioTimeline.incrementWithoutUrl();
//                                                next();
//                                            }
//                                        }
//                                    });
//                        }
//                    }
//                    QueryManager.getInstance().scrobbleOffline();
//                }
//            }
        }
    }
}
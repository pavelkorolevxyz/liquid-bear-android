package com.pillowapps.liqear.audio;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;

import javax.inject.Inject;

public class MusicService extends Service {

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

    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;

    @Inject
    MediaPlayerManager mediaPlayerManager;

    @Inject
    RemoteControlManager remoteControlManager;

    @Inject
    AudioFocusManager audioFocusManager;

    @Override
    public void onCreate() {
        super.onCreate();
        LBApplication.get(this).applicationComponent().inject(this);

        mediaPlayerManager.mute(true);

        initMediaPlayer();
        initLocks();

        registerRemote();
        registerStateReceivers();
    }

    private void initMediaPlayer() {
        mediaPlayerManager.init();
    }

    private void initLocks() {
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getString(R.string.app_name));
        WifiManager wifimanager = (WifiManager) LBApplication.getAppContext().getSystemService(Context.WIFI_SERVICE);
        wifiLock = wifimanager.createWifiLock(getString(R.string.app_name));
    }

    private void registerRemote() {
        remoteControlManager.register();
    }

    private void unregisterRemote() {
        remoteControlManager.unregister();
    }

    private void registerStateReceivers() {
        audioFocusManager.init();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}

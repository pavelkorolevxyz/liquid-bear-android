package com.pillowapps.liqear.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.audio.MusicService;
import com.pillowapps.liqear.helpers.ServiceConnectionListener;

public class MusicServiceManager {

    private static MusicServiceManager instance;

    private MusicService musicService;
    private ServiceConnection serviceConnection;

    private MusicServiceManager() {
    }

    public static synchronized MusicServiceManager getInstance() {
        if (instance == null) {
            instance = new MusicServiceManager();
        }
        return instance;
    }

    public void startService(Context context, final ServiceConnectionListener listener) {
        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
                musicService = binder.getService();
                listener.onServiceConnected();
            }

            public void onServiceDisconnected(ComponentName arg0) {
            }
        };
        Intent intent = new Intent(LBApplication.getAppContext(), MusicService.class);
        context.startService(intent);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void pause() {
        musicService.pause();
    }

    public void setTimer(int seconds) {
        musicService.setTimer(seconds);
    }

    public void stopService(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        try {
            if (serviceConnection != null) {
                context.unbindService(serviceConnection);
            }
        } catch (IllegalArgumentException ignored) {
            // No operations.
        }
        musicService.stopService(intent);
    }

    public void play(int index) {
        musicService.play(index);
    }

    public boolean isPrepared() {
        return musicService.isPrepared();
    }

    public int getCurrentPositionPercent() {
        return musicService.getCurrentPositionPercent();
    }

    public int getCurrentPosition() {
        return musicService.getCurrentPosition();
    }

    public int getDuration() {
        return musicService.getDuration();
    }

    public void updateWidgets() {
        musicService.updateWidgets();
    }

    public void playPause() {
        musicService.playPause();
    }

    public void next() {
        musicService.next();
    }

    public void prev() {
        musicService.prev();
    }

    public void seekTo(int position) {
        musicService.seekTo(position);
    }

    public void startPlayProgressUpdater() {
        musicService.startPlayProgressUpdater();
    }

    public void stopPlayProgressUpdater() {
        musicService.stopPlayProgressUpdater();
    }
}

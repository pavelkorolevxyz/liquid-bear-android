package com.pillowapps.liqear.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.pillowapps.liqear.audio.MusicService;
import com.pillowapps.liqear.listeners.OnServiceConnectedListener;

public class MusicServiceManager {

    private MusicService musicService;
    private ServiceConnection serviceConnection;

    public MusicService getService() {
        return musicService;
    }

    public void startServiceAsync(Context context, final OnServiceConnectedListener listener) {
        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
                musicService = binder.getService();
                musicService.restore();
                listener.onServiceConnected();
            }

            public void onServiceDisconnected(ComponentName arg0) {
            }
        };
        Intent intent = new Intent(context, MusicService.class);
        context.startService(intent);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void pause() {
        musicService.pause();
    }

    public void setTimer(int minutes) {
        musicService.setTimer(minutes);
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

    public void play(int index, boolean autoplay) {
        if (musicService == null) {
            return;
        }
        musicService.play(index, autoplay);
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

    public void startUpdaters() {
        musicService.startUpdaters();
    }

    public void stopUpdaters() {
        musicService.stopUpdaters();
    }

    public void changeCurrentTrackUrl(int newPosition, String url) {
        musicService.changeUrl(newPosition, url);
    }

    public void restore() {
        if (musicService == null) {
            return;
        }
        musicService.restore();
    }
}

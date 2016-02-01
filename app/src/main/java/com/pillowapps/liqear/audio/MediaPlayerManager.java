package com.pillowapps.liqear.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.entities.PlayingState;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.events.BufferizationEvent;
import com.pillowapps.liqear.entities.events.PlayEvent;
import com.pillowapps.liqear.entities.events.PlayWithoutIconEvent;
import com.pillowapps.liqear.entities.events.PreparedEvent;
import com.pillowapps.liqear.entities.events.UpdatePositionEvent;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.NetworkUtils;

import javax.inject.Inject;

import timber.log.Timber;

public class MediaPlayerManager implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private Context context;

    private Timeline timeline;

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    @Inject
    public MediaPlayerManager(Context context, Timeline timeline, MediaPlayer mediaPlayer, AudioManager audioManager) {
        this.context = context;
        this.timeline = timeline;
        this.mediaPlayer = mediaPlayer;
        this.audioManager = audioManager;
    }

    public void init() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setAudioSessionId(Constants.LIQUID_BEAR_ID);
        mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
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
        Timber.e("MEDIA PLAYER ERROR:" + what);
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
//        currentPositionPercent = 0;
        if (timeline.getPlaylistTracks().size() == 0) {
            return;
        }
        prepared = true;
        LBApplication.BUS.post(new PreparedEvent());
        Track currentTrack = timeline.getCurrentTrack();
        currentTrack.setDuration(mediaPlayer.getDuration());
        hasDataSource = true;
        scrobbled = false;
        secondsTrackPlayed = 0;
        if (NetworkUtils.isOnline()) {
            if (!currentTrack.getArtist().equals(timeline.getPreviousArtist())) {
                getArtistInfo(currentTrack.getArtist(), AuthorizationInfoManager.getLastfmName());
            }
            getTrackInfo(currentTrack);
        }
        saveTrackState();
        if (timeline.isStartPlayingOnPrepared()) {
            timeline.setPlayingState(PlayingState.PLAYING);
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
            if (timeline.getPlayingStateBeforeCall() == PlayingState.PLAYING) {
                play();
            }
            registerRemote();
        }
    }

    public void release() {
        mediaPlayer.release();
    }

    public void mute(boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, enable ? AudioManager.ADJUST_MUTE : AudioManager.ADJUST_UNMUTE, 0);
        } else {
            //noinspection deprecation
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, enable);
        }
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void play() {
        mediaPlayer.play();
    }


}

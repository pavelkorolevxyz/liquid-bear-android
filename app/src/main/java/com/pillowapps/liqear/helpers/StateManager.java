package com.pillowapps.liqear.helpers;

import android.content.SharedPreferences;

import com.pillowapps.liqear.audio.MusicService;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.CompletionCallback;
import com.pillowapps.liqear.entities.RepeatMode;
import com.pillowapps.liqear.entities.ShuffleMode;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.models.PlaylistModel;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class StateManager {

    private PlaylistModel playlistModel;
    private Timeline timeline;

    public StateManager(PlaylistModel playlistModel, Timeline timeline) {
        this.playlistModel = playlistModel;
        this.timeline = timeline;
    }

    public void savePlaylistState(MusicService service) {
        saveTrackState();
        SharedPreferences.Editor editor = SharedPreferencesManager.getPreferences().edit();
        if (service != null) {
            editor.putInt(Constants.CURRENT_POSITION, service.getCurrentPosition());
            editor.putInt(Constants.CURRENT_BUFFER, service.getCurrentBuffer());
            boolean shuffleOn = timeline.getShuffleMode() == ShuffleMode.SHUFFLE;
            boolean repeatOn = timeline.getRepeatMode() == RepeatMode.REPEAT;
            editor.putBoolean(Constants.SHUFFLE_MODE_ON, shuffleOn);
            editor.putBoolean(Constants.REPEAT_MODE_ON, repeatOn);
            editor.putInt(Constants.CURRENT_INDEX, timeline.getIndex());
        }
        editor.apply();
//        new PlaylistModel().saveMainPlaylist();
    }

    public void saveTrackState() {
        SharedPreferences.Editor editor = SharedPreferencesManager.getPreferences().edit();
        final Track currentTrack = timeline.getCurrentTrack();
        if (timeline.getPlaylistTracks() != null
                && timeline.getPlaylistTracks().size() != 0
                && currentTrack != null) {
            editor.putString(Constants.ARTIST, currentTrack.getArtist());
            editor.putString(Constants.TITLE, currentTrack.getTitle());
            editor.putInt(Constants.DURATION, currentTrack.getDuration());
        }
        editor.putInt(Constants.CURRENT_INDEX, timeline.getIndex());
        editor.apply();
    }

    public void restorePlaylistState(final CompletionCallback completionCallback) {
        Timber.d("restore state");
        final long time = System.currentTimeMillis();
        playlistModel.getMainPlaylist()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(playlist -> {
                    timeline.setPlaylist(playlist);
                    Timber.d("time = " + (System.currentTimeMillis() - time));
                    completionCallback.onCompleted();
                });
    }

    public void restoreTrackState() {

    }
}

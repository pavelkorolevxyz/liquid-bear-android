package com.pillowapps.liqear.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.pillowapps.liqear.audio.MusicService;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.CompletionCallback;
import com.pillowapps.liqear.entities.RepeatMode;
import com.pillowapps.liqear.entities.RestoreData;
import com.pillowapps.liqear.entities.ShuffleMode;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.models.PlaylistModel;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class StateManager {

    private Context context;
    private PlaylistModel playlistModel;
    private Timeline timeline;
    private SavesManager savesManager;

    public StateManager(Context context, SavesManager savesManager, PlaylistModel playlistModel, Timeline timeline) {
        this.context = context;
        this.savesManager = savesManager;
        this.playlistModel = playlistModel;
        this.timeline = timeline;
    }

    public void savePlaylistState(MusicService service) {
        if (service == null) {
            return;
        }
        int currentPosition = service.getCurrentPosition();
        int currentBuffer = service.getCurrentBuffer();
        boolean shuffleOn = timeline.getShuffleMode() == ShuffleMode.SHUFFLE;
        boolean repeatOn = timeline.getRepeatMode() == RepeatMode.REPEAT;
        int index = timeline.getIndex();

        savesManager.saveCurrentPosition(currentPosition);
        savesManager.saveBuffer(currentBuffer);
        savesManager.saveShuffleMode(shuffleOn);
        savesManager.saveRepeatMode(repeatOn);
        savesManager.saveCurrentIndex(index);

        Track currentTrack = timeline.getCurrentTrack();
        if (currentTrack == null) {
            return;
        }
        String artist = currentTrack.getArtist();
        String title = currentTrack.getTitle();
        int duration = currentTrack.getDuration();

        savesManager.saveArtist(artist);
        savesManager.saveTitle(title);
        savesManager.saveDuration(duration);
    }

    public void restorePlaylistState(final CompletionCallback completionCallback) {
        Timber.d("getRestoreData state");
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

    public RestoreData getRestoreData() {
        String artist = savesManager.getArtist();
        String title = savesManager.getTitle();
        int currentIndex = savesManager.getCurrentIndex();
        int position = savesManager.getPosition();

        return new RestoreData(artist, title, currentIndex, position);
    }

    public boolean toggleSearchVisibility() {
        SharedPreferences savePreferences = SharedPreferencesManager.getSavePreferences(context);
        boolean visibility = !savePreferences.getBoolean(Constants.SEARCH_PLAYLIST_VISIBILITY, false);
        savePreferences.edit().putBoolean(Constants.SEARCH_PLAYLIST_VISIBILITY, visibility).apply();
        return visibility;
    }
}

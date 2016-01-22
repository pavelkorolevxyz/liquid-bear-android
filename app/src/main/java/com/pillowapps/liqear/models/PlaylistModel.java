package com.pillowapps.liqear.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.StorageManager;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;

import java.util.Collections;
import java.util.List;

import rx.Observable;

public class PlaylistModel {

    public Observable<Long> saveMainPlaylist(@NonNull Context context, Playlist playlist) {
        if (playlist == null) return Observable.empty();
        playlist.setMainPlaylist(true);
        StorageManager storageManager = StorageManager.getInstance(context);

        return storageManager.findAndDeleteMainPlaylist()
                .onErrorReturn(throwable -> DeleteResult.newInstance(0, Collections.emptySet()))
                .flatMap(deleteResult -> storageManager.savePlaylist(playlist));
    }

    public Observable<Playlist> getMainPlaylist(@NonNull Context context) {
        return StorageManager.getInstance(context).getMainPlaylist();
    }

    public Observable<List<Playlist>> getPlaylists(@NonNull Context context) {
        return StorageManager.getInstance(context).getPlaylists();
    }

    public Observable removePlaylist(@NonNull Context context, @NonNull Long id) {
        return StorageManager.getInstance(context).findAndDeletePlaylist(id);
    }

    public Observable renamePlaylist(@NonNull Context context, @NonNull Long id, @NonNull String newTitle) {
        return StorageManager.getInstance(context).renamePlaylist(id, newTitle);
    }

    public Observable addTrackToPlaylist(@NonNull Context context, @NonNull Long playlistId, @NonNull Track track) {
        return StorageManager.getInstance(context).saveTrackToPlaylist(playlistId, track);
    }

    public Observable<Long> savePlaylist(@NonNull Context context, String title, @NonNull List<Track> tracks) {
        return StorageManager.getInstance(context).savePlaylist(new Playlist(title, tracks));
    }

    public Observable<Playlist> getPlaylist(@NonNull Context context, @NonNull final Long playlistId) {
        return StorageManager.getInstance(context).getPlaylist(playlistId);
    }

}

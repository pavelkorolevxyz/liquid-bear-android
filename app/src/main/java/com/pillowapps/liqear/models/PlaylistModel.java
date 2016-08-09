package com.pillowapps.liqear.models;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.PlaylistsStorage;

import java.util.List;

import rx.Observable;

public class PlaylistModel {

    private PlaylistsStorage storageManager;

    public PlaylistModel(PlaylistsStorage storageManager) {
        this.storageManager = storageManager;
    }

    public Observable<Long> saveMainPlaylist(Playlist playlist) {
        if (playlist == null) {
            return Observable.empty();
        }
        playlist.setMainPlaylist(true);

        return storageManager.findAndDeleteMainPlaylist()
//                .onErrorReturn(throwable -> DeleteResult.newInstance(0, Collections.emptySet()))
                .flatMap(deleteResult -> storageManager.savePlaylist(playlist));
    }

    public Observable<Playlist> getMainPlaylist() {
        return storageManager.getMainPlaylist();
    }

    public Observable<List<Playlist>> getPlaylists() {
        return storageManager.getPlaylists();
    }

    public Observable<Object> removePlaylist(@NonNull Long id) {
        return storageManager.findAndDeletePlaylist(id).map(deleteResult -> deleteResult);
    }

    public Observable<Object> renamePlaylist(@NonNull Long id, @NonNull String newTitle) {
        return storageManager.renamePlaylist(id, newTitle).map(putResult -> putResult);
    }

    public Observable<Object> addTrackToPlaylist(@NonNull Long playlistId, @NonNull Track track) {
        return storageManager.saveTrackToPlaylist(playlistId, track).map(putResult -> putResult);
    }

    public Observable<Long> savePlaylist(String title, @NonNull List<Track> tracks) {
        return storageManager.savePlaylist(new Playlist(title, tracks));
    }

    public Observable<Playlist> getPlaylist(@NonNull final Long playlistId) {
        return storageManager.getPlaylist(playlistId);
    }

}

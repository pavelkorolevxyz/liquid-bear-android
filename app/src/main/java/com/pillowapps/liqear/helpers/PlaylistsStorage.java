package com.pillowapps.liqear.helpers;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;

import java.util.List;

import rx.Observable;

public interface PlaylistsStorage {

    //todo get rid of storio dependency
    Observable<DeleteResult> findAndDeleteMainPlaylist();

    Observable<DeleteResult> findAndDeletePlaylist(@NonNull Long playlistId);

    Observable<Playlist> getMainPlaylist();

    Observable<List<Playlist>> getPlaylists();

    Observable<Playlist> getPlaylist(@NonNull Long playlistId);

    /**
     * @return ID of saved playlist
     */
    Observable<Long> savePlaylist(Playlist playlist);

    /**
     * @return ID of playlist where tracks were saved
     */
    Observable<Long> saveTracksToPlaylist(@NonNull Long playlistId, List<Track> tracks);

    /**
     * @return ID of playlist where tracks were saved
     */
    Observable<PutResult> saveTrackToPlaylist(@NonNull Long playlistId, Track track);

    Observable<PutResult> renamePlaylist(@NonNull Long id, String newTitle);
}

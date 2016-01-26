package com.pillowapps.liqear.helpers;

import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;

import java.util.List;

import rx.Observable;

public interface PlaylistsStorage {

    //todo get rid of storio dependency
    Observable<DeleteResult> findAndDeleteMainPlaylist();

    Observable findAndDeletePlaylist(Long playlistId);

    Observable<Playlist> getMainPlaylist();

    Observable<List<Playlist>> getPlaylists();

    Observable<Playlist> getPlaylist(Long playlistId);

    /**
     * @return ID of saved playlist
     */
    Observable<Long> savePlaylist(Playlist playlist);

    /**
     * @return ID of playlist where tracks were saved
     */
    Observable<Long> saveTracksToPlaylist(Long playlistId, List<Track> tracks);

    /**
     * @return ID of playlist where tracks were saved
     */
    Observable saveTrackToPlaylist(Long playlistId, Track track);

    Observable renamePlaylist(Long id, String newTitle);
}

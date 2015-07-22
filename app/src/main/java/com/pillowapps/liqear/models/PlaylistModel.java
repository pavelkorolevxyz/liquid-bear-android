package com.pillowapps.liqear.models;

import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.GetPlaylistCallback;
import com.pillowapps.liqear.callbacks.GetPlaylistListCallback;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Playlist$Table;
import com.pillowapps.liqear.entities.Track;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Update;

import java.util.List;

import timber.log.Timber;


public class PlaylistModel {

    public void saveMainPlaylist() {
        Playlist playlist = Timeline.getInstance().getPlaylist();
        if (playlist == null) return;
        clearMainPlaylist();
        playlist.setMainPlaylist(true);

        playlist.save();

    }

    public void getMainPlaylist(final GetPlaylistCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                From<Playlist> from = new Select().from(Playlist.class);
                Timber.d(from.queryList().toString());
                callback.onCompleted(from.where(Condition.column(Playlist$Table.MAINPLAYLIST).is(true)).querySingle());
            }
        }).start();
    }

    private void clearMainPlaylist() {
        new Delete().from(Playlist.class).where(Condition.column(Playlist$Table.MAINPLAYLIST).is(true)).queryClose();
    }

    public void getSavedPlaylists(final GetPlaylistListCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                From<Playlist> from = new Select().from(Playlist.class);
                Timber.d(from.queryList().toString());
                callback.onCompleted(from.where(Condition.column(Playlist$Table.MAINPLAYLIST).is(false)).queryList());
            }
        }).start();
    }

    public void removePlaylist(Long id) {
        new Delete().from(Playlist.class).where(Condition.column(Playlist$Table.ID).is(id)).queryClose();
    }

    public void renamePlaylist(Long id, String newTitle) {
        Update.table(Playlist.class).set(Condition.column(Playlist$Table.TITLE).eq(newTitle))
                .where(Condition.column(Playlist$Table.ID).is(id)).queryClose();
    }

    public void addTrackToPlaylist(Playlist playlist, Track track) {
        track.associatePlaylist(playlist);
        track.save();
    }

    public long addPlaylist(String title, List<Track> tracks) {
        Playlist playlist = new Playlist();
        playlist.setTitle(title);
        playlist.setTracks(tracks);
        playlist.save();
        return playlist.getId();
    }

    public void getPlaylist(final long pid, final GetPlaylistCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                From<Playlist> from = new Select().from(Playlist.class);
                Timber.d(from.queryList().toString());
                callback.onCompleted(from.where(Condition.column(Playlist$Table.ID).is(pid)).querySingle());
            }
        }).start();
    }
}

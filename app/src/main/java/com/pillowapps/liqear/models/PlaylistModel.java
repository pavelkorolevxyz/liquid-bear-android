package com.pillowapps.liqear.models;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.GetPlaylistCallback;
import com.pillowapps.liqear.callbacks.GetPlaylistListCallback;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.StorageManager;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class PlaylistModel {

    public void saveMainPlaylist() {
        Playlist playlist = Timeline.getInstance().getPlaylist();
        if (playlist == null) return;
        clearMainPlaylist();
        playlist.setMainPlaylist(true);
        StorageManager.getInstance(LBApplication.getAppContext()).savePlaylist(playlist)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(putResult -> {
                    Timber.d(putResult.toString());
                });
    }

    public void getMainPlaylist(final GetPlaylistCallback callback) {
        StorageManager.getInstance(LBApplication.getAppContext()).getMainPlaylist()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onCompleted);
    }

    private void clearMainPlaylist() {
        StorageManager.getInstance(LBApplication.getAppContext()).deleteMainPlaylist().subscribe();
    }

    public void getSavedPlaylists(final GetPlaylistListCallback callback) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                From<Playlist> from = new Select().from(Playlist.class);
//                Timber.d(from.queryList().toString());
//                callback.onCompleted(from.where(Condition.column(Playlist$Table.MAINPLAYLIST).is(false)).queryList());
//            }
//        }).start();
    }

    public void removePlaylist(Long id) {
//        new Delete().from(Playlist.class).where(Condition.column(Playlist$Table.ID).is(id)).queryClose();
    }

    public void renamePlaylist(Long id, String newTitle) {
//        Update.table(Playlist.class).set(Condition.column(Playlist$Table.TITLE).eq(newTitle))
//                .where(Condition.column(Playlist$Table.ID).is(id)).queryClose();
    }

    public void addTrackToPlaylist(Playlist playlist, Track track) {
//        track.associatePlaylist(playlist);
//        track.save();
    }

    public long addPlaylist(String title, List<Track> tracks) {
        Playlist playlist = new Playlist();
        playlist.setTitle(title);
        playlist.setTracks(tracks);
//        playlist.save();
        return playlist.getId();
    }

    public void getPlaylist(final long pid, final GetPlaylistCallback callback) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                From<Playlist> from = new Select().from(Playlist.class);
//                Timber.d(from.queryList().toString());
//                callback.onCompleted(from.where(Condition.column(Playlist$Table.ID).is(pid)).querySingle());
//            }
//        }).start();
    }
}

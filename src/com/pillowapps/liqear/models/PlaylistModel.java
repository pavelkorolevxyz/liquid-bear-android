package com.pillowapps.liqear.models;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.entities.Playlist;

import io.realm.Realm;
import io.realm.RealmQuery;
import timber.log.Timber;

public class PlaylistModel {
    private Realm realm = LBApplication.realm;

    public void saveMainPlaylist() {
        realm.beginTransaction();
        realm.where(Playlist.class).equalTo("mainPlaylist", true).findAll().clear(); // delete previous main playlist
        Playlist playlist = Timeline.getInstance().getPlaylist();
        playlist.setMainPlaylist(true);
        realm.copyToRealm(playlist);
        realm.commitTransaction();
    }

    public Playlist getMainPlaylist() {
        RealmQuery<Playlist> query = realm.where(Playlist.class).equalTo("mainPlaylist", true);
        return query.findFirst();
    }

    public void savePlaylist(Playlist playlist) {
        realm.beginTransaction();
        realm.copyToRealm(playlist);
        realm.commitTransaction();
    }

    public Playlist getPlaylist() {
        //todo
        return null;
    }

    public void removePlaylist() {
        //todo
    }

    public void renamePlaylist() {
        //todo
    }
}

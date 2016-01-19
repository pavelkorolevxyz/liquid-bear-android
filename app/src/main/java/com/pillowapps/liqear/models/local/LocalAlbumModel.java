package com.pillowapps.liqear.models.local;

import android.database.Cursor;
import android.provider.MediaStore;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.callbacks.LocalDataCallback;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Track;

import java.util.ArrayList;
import java.util.List;

import inaka.com.tinytask.DoThis;
import inaka.com.tinytask.TinyTask;

public class LocalAlbumModel {

    @SuppressWarnings("unchecked")
    public void getAllAlbums(final LocalDataCallback<List<Album>> callback) {
        TinyTask.perform(() -> {
            String[] projection = {
                    MediaStore.Audio.Albums._ID,
                    MediaStore.Audio.Albums.ARTIST,
                    MediaStore.Audio.Albums.ALBUM,
                    MediaStore.Audio.Albums.ALBUM_ART
            };

            Cursor cursor = LBApplication.getAppContext().getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    projection, null, null, null);
            List<Album> albums = new ArrayList<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String artist = cursor.getString(1);
                    String title = cursor.getString(2);
                    String albumId = cursor.getString(0);
                    String urlImage = cursor.getString(3);
                    Album album = new Album(artist, title, null, albumId, urlImage);
                    if (album.getTitle().length() > 0)
                        albums.add(album);
                }
                cursor.close();
            }
            return albums;
        }).whenDone(new DoThis<List<Album>>() {
            @Override
            public void ifOK(List<Album> albums) {
                callback.success(albums);
            }

            @Override
            public void ifNotOK(Exception e) {
                callback.failure(e.getMessage());
            }
        }).go();
    }

    @SuppressWarnings("unchecked")
    public void getTracksFromAlbum(final String albumId, final LocalDataCallback<List<Track>> callback) {
        TinyTask.perform(() -> {
            String selection = MediaStore.Audio.Media.ALBUM_ID + "=?";

            String[] projection = {
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
            };
            String[] whereVal = {albumId};

            Cursor cursor = LBApplication.getAppContext().getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection, selection, whereVal, null);
            List<Track> tracks = new ArrayList<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Track track = new Track(cursor.getString(0), cursor.getString(1),
                            cursor.getString(2), true);
                    if (track.getArtist().length() > 0 && track.getTitle().length() > 0)
                        tracks.add(track);
                }
                cursor.close();
            }
            return tracks;
        }).whenDone(new DoThis<List<Track>>() {
            @Override
            public void ifOK(List<Track> tracks) {
                callback.success(tracks);
            }

            @Override
            public void ifNotOK(Exception e) {
                callback.failure(e.getMessage());
            }
        }).go();
    }

}

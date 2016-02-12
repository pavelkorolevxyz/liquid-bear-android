package com.pillowapps.liqear.models.local;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.callbacks.LocalDataCallback;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.Track;

import java.util.ArrayList;
import java.util.List;

import inaka.com.tinytask.DoThis;
import inaka.com.tinytask.TinyTask;

public class LocalArtistModel {

    private Context context;

    public LocalArtistModel(Context context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public void getAllArtists(final LocalDataCallback<List<Artist>> callback) {
        TinyTask.perform(() -> {
            String[] projection = {
                    MediaStore.Audio.Artists._ID,
                    MediaStore.Audio.Artists.ARTIST,
            };

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                    projection, null, null, null);
            List<Artist> artists = new ArrayList<>();

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(1);
                    String id = cursor.getString(0);
                    Artist artist = new Artist(name, id);
                    if (artist.getName().length() > 0)
                        artists.add(artist);
                }
                cursor.close();
            }
            return artists;
        }).whenDone(new DoThis<List<Artist>>() {
            @Override
            public void ifOK(List<Artist> artists) {
                callback.success(artists);
            }

            @Override
            public void ifNotOK(Exception e) {
                callback.failure(e.getMessage());
            }
        }).go();
    }

    @SuppressWarnings("unchecked")
    public void getTracksFromArtist(final String artistId, final LocalDataCallback<List<Track>> callback) {
        TinyTask.perform(() -> {
            String selection = MediaStore.Audio.Media.ARTIST_ID + "=?";

            String[] projection = {
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
            };

            String[] whereVal = {artistId};

            Cursor cursor = context.getContentResolver().query(
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

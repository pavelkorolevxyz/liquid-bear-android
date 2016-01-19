package com.pillowapps.liqear.models.local;

import android.database.Cursor;
import android.provider.MediaStore;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.callbacks.LocalDataCallback;
import com.pillowapps.liqear.entities.Track;

import java.util.ArrayList;
import java.util.List;

import inaka.com.tinytask.DoThis;
import inaka.com.tinytask.TinyTask;

public class LocalTrackModel {

    @SuppressWarnings("unchecked")
    public void getAllTracks(final LocalDataCallback<List<Track>> callback) {
        TinyTask.perform(() -> {
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
            String[] projection = {
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
            };

            Cursor cursor = LBApplication.getAppContext().getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection, selection, null, null);

            List<Track> tracks = new ArrayList<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String artist = cursor.getString(0);
                    String title = cursor.getString(1);
                    String url = cursor.getString(2);
                    tracks.add(new Track(artist, title, url, true));
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

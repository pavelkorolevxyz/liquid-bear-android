package com.pillowapps.liqear.models;

import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.RestoringPlaylistCallback;
import com.pillowapps.liqear.entities.Playlist;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;


public class PlaylistModel {

    public void saveMainPlaylist() {
        Playlist playlist = Timeline.getInstance().getPlaylist();
        if (playlist == null) return;
        clearMainPlaylist();
        playlist.setMainPlaylist(true);
        playlist.save();
    }

    public void getMainPlaylist(final RestoringPlaylistCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                callback.onCompleted(new Select().from(Playlist.class).where(Condition.column("mainPlaylist").is(true)).querySingle());
            }
        }).start();
    }

    private void clearMainPlaylist() {
        new Delete().from(Playlist.class).where(Condition.column("mainPlaylist").is(true));
    }

//    public void savePlaylist(Playlist playlist) {
//        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
//        cupboard().withDatabase(writableDatabase).put(playlist);
//        writableDatabase.close();
//    }
//
//    public List<Playlist> getPlaylists() {
//        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
//        List<Playlist> playlists = cupboard().withDatabase(readableDatabase).query(Playlist.class)
//                .withSelection("mainPlaylist = ?", "false").list();
//        readableDatabase.close();
//        return playlists;
//    }
//
//    public void removePlaylist(Long id) {
//        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
//        cupboard().withDatabase(writableDatabase).delete(Playlist.class, id);
//        writableDatabase.close();
//    }
//
//    public void renamePlaylist(Long id, String newTitle) {
//        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
//        ContentValues values = new ContentValues(1);
//        values.put("title", newTitle);
//        cupboard().withDatabase(writableDatabase).update(Playlist.class, values, "_id = ?",
//                String.valueOf(id));
//        writableDatabase.close();
//    }
}

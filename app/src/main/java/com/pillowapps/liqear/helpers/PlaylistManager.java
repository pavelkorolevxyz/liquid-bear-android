package com.pillowapps.liqear.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class PlaylistManager {
    private static final String DATABASE_TITLE = "LiqearDatabase3";
    private static final String SCROBBLE_TABLE = "TracksToScrobble";
    private static final String UNSAVED_PLAYLIST_TABLE = "UnsavedPlaylist";
    private static final String PLAYLISTS_TABLE = "Playlists";
    private static final String TRACKS_TABLE = "Tracks";
    private static final int DATABASE_VERSION = 1;
    private static PlaylistManager instance;

    private PlaylistManager() {
    }

    public static PlaylistManager getInstance() {
        if (instance == null) {
            instance = new PlaylistManager();
        }
        return instance;
    }

    public long addPlaylist(final String title, final List<Track> playlist) {
        long pid = -1;
        final List<Track> trackList = Collections.unmodifiableList(playlist);
        if (trackList != null) {
            Context context = LBApplication.getAppContext();
            SQLiteDatabase sampleDB = null;
            try {
                sampleDB = context.openOrCreateDatabase(DATABASE_TITLE, Context.MODE_PRIVATE, null);
                sampleDB.beginTransaction();
                createUrlColumnIfNotExists(sampleDB, PLAYLISTS_TABLE);
                String sqlQuery = "CREATE TABLE IF NOT EXISTS "
                        + PLAYLISTS_TABLE
                        + "(pid INTEGER PRIMARY KEY NOT NULL, title VARCHAR NOT NULL);";
                sampleDB.execSQL(sqlQuery);
                ContentValues values = new ContentValues();
                values.put("title", title);
                pid = sampleDB.insert(PLAYLISTS_TABLE, null, values);
                sampleDB.execSQL("CREATE TABLE IF NOT EXISTS "
                        + TRACKS_TABLE
                        + " (`tid` INTEGER PRIMARY KEY NOT NULL, `artist` VARCHAR NOT NULL, `title` VARCHAR NOT NULL, `pid` INTEGER NOT NULL, `url` VARCHAR);");
                for (ListIterator<Track> itr = trackList.listIterator(); itr.hasNext(); ) {
                    Track track = itr.next();
                    values.clear();
                    values.put("artist", track.getArtist());
                    values.put("title", track.getTitle());
                    values.put("pid", pid);
                    String url = track.isLocal() ? track.getUrl() : "";
                    values.put("url", url);
                    sampleDB.insert(TRACKS_TABLE, null, values);
                }
                sampleDB.setTransactionSuccessful();
            } catch (SQLiteException e) {
                // No operations.
                e.printStackTrace();
            } finally {
                if (sampleDB != null) {
                    sampleDB.endTransaction();
                    sampleDB.close();
                }
            }

        }
        return pid;
    }

    public void removePlaylist(final long pid) {
        new Thread(new Runnable() {
            public void run() {
                Context context = LBApplication.getAppContext();
                SQLiteDatabase sampleDB = null;
                try {
                    sampleDB = context.openOrCreateDatabase(
                            DATABASE_TITLE, Context.MODE_PRIVATE, null);
                    Cursor c = sampleDB.rawQuery("SELECT pid FROM "
                            + PLAYLISTS_TABLE + " WHERE pid=" + pid, null);
                    long pid = -1;
                    if (c != null) {
                        if (c.moveToFirst()) {
                            pid = c.getLong(c.getColumnIndex("pid"));
                        }
                    }
                    sampleDB.beginTransaction();
                    sampleDB.delete(PLAYLISTS_TABLE, "pid=" + pid, null);
                    sampleDB.delete(TRACKS_TABLE, "pid=" + pid, null);
                    sampleDB.setTransactionSuccessful();
                } catch (SQLiteException e) {
                    // No operations.
                    e.printStackTrace();
                } finally {
                    if (sampleDB != null) {
                        sampleDB.endTransaction();
                        sampleDB.close();
                    }
                }
            }

        }).start();
    }

    public void renamePlaylist(final long pid, final String title) {
        new Thread(new Runnable() {
            public void run() {
                Context context = LBApplication.getAppContext();
                SQLiteDatabase sampleDB = null;
                try {
                    sampleDB = context.openOrCreateDatabase(
                            DATABASE_TITLE, Context.MODE_PRIVATE, null);
                    ContentValues values = new ContentValues();
                    values.put("title", title);
                    sampleDB.beginTransaction();
                    sampleDB.update(PLAYLISTS_TABLE, values, "pid=" + pid, null);
                    sampleDB.setTransactionSuccessful();
                } catch (SQLiteException e) {
                    // No operations.
                    e.printStackTrace();
                } finally {
                    if (sampleDB != null) {
                        sampleDB.endTransaction();
                        sampleDB.close();
                    }
                }
            }

        }).start();
    }

    public List<Track> getPlaylist(final long pid) {
        List<Track> trackList = new ArrayList<Track>();
        Context context = LBApplication.getAppContext();
        SQLiteDatabase sampleDB = null;
        try {
            sampleDB = context.openOrCreateDatabase(DATABASE_TITLE,
                    Context.MODE_PRIVATE, null);
            createUrlColumnIfNotExists(sampleDB, TRACKS_TABLE);
            Cursor c = sampleDB.rawQuery("SELECT tid, artist, title, url FROM "
                    + TRACKS_TABLE + " WHERE pid=" + pid, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        Long trackId = c.getLong(c.getColumnIndex("tid"));
                        String artist = c.getString(c.getColumnIndex("artist"));
                        String title = c.getString(c.getColumnIndex("title"));
                        String url = c.getString(c.getColumnIndex("url"));
                        trackList.add(new Track(artist, title, url));
                    } while (c.moveToNext());
                }
            }

        } catch (SQLiteException e) {
            // No operations.
            e.printStackTrace();
        } finally {
            if (sampleDB != null) {
                sampleDB.close();
            }
        }
        return trackList;
    }

    public List<Playlist> getPlaylists() {
        List<Playlist> playlists = new ArrayList<Playlist>();
        Context context = LBApplication.getAppContext();
        SQLiteDatabase sampleDB = null;
        try {
            sampleDB = context.openOrCreateDatabase(DATABASE_TITLE,
                    Context.MODE_PRIVATE, null);
            Cursor c = sampleDB.rawQuery("SELECT pid, title FROM "
                    + PLAYLISTS_TABLE, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        Playlist playlist = new Playlist();
                        playlist.setId(c.getLong(c.getColumnIndex("pid")));
                        playlist.setTitle(c.getString(c.getColumnIndex("title")));
                        playlists.add(playlist);
                    } while (c.moveToNext());
                }
            }
        } catch (SQLiteException e) {
            // No operations.
            e.printStackTrace();
        } finally {
            if (sampleDB != null) {
                sampleDB.close();
            }
        }
        return playlists;
    }

    public void saveUnsavedPlaylist(final List<Track> playlist) {
        final List<Track> trackList = Collections.unmodifiableList(playlist);
        new Thread(new Runnable() {
            public void run() {
                String table = UNSAVED_PLAYLIST_TABLE;
                if (trackList == null || trackList.size() <= 0) {
                    return;
                }
                Context context = LBApplication.getAppContext();
                SQLiteDatabase sampleDB = null;
                try {
                    sampleDB = context.openOrCreateDatabase(DATABASE_TITLE, Context.MODE_PRIVATE,
                            null);
                    sampleDB.beginTransaction();
                    createUrlColumnIfNotExists(sampleDB, table);
                    sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " + table
                            + " (artist VARCHAR, title VARCHAR, url VARCHAR);");
                    sampleDB.delete(table, "artist is not null", null);
                    saveTracklistLoop(table, sampleDB, trackList);
                    sampleDB.setTransactionSuccessful();
                } catch (SQLiteException e) {
                    // No operations.
                    e.printStackTrace();
                } finally {
                    if (sampleDB != null) {
                        sampleDB.endTransaction();
                        sampleDB.close();
                    }
                }
            }
        }).start();
    }

    private synchronized void saveTracklistLoop(String table, SQLiteDatabase sampleDB,
                                                List<Track> trackList) {
        for (ListIterator<Track> itr = trackList.listIterator(); itr.hasNext(); ) {
            Track track = itr.next();
            String url = track.isLocal() ? track.getUrl() : "";
            sampleDB.execSQL(String.format(
                    "INSERT  INTO %s Values (%s,%s,%s);",
                    table,
                    DatabaseUtils.sqlEscapeString(track.getArtist()),
                    DatabaseUtils.sqlEscapeString(track.getTitle()),
                    DatabaseUtils.sqlEscapeString(url)
            )
            );

        }
    }

    public List<Track> loadPlaylist() {
        String table = UNSAVED_PLAYLIST_TABLE;
        List<Track> trackList = new ArrayList<Track>();
        Context context = LBApplication.getAppContext();
        SQLiteDatabase sampleDB = null;
        try {
            sampleDB = context.openOrCreateDatabase(DATABASE_TITLE,
                    Context.MODE_PRIVATE, null);
            createUrlColumnIfNotExists(sampleDB, table);
            Cursor c = sampleDB.rawQuery("SELECT artist, title, url FROM " + table, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String artist = c.getString(c.getColumnIndex("artist"));
                        String title = c.getString(c.getColumnIndex("title"));
                        String url = c.getString(c.getColumnIndex("url"));
                        boolean local = !url.isEmpty();
                        if (Utils.isOnline() || local) {
                            trackList.add(new Track(artist, title, url, local));
                        }
                    } while (c.moveToNext());
                }
            }
        } catch (SQLiteException e) {
            // No operations.
            e.printStackTrace();
        } finally {
            if (sampleDB != null) {
                sampleDB.close();
            }
        }
        return trackList;
    }

    private void createUrlColumnIfNotExists(SQLiteDatabase sampleDB, String table) {
        try {
            Cursor checkUrlCursor = sampleDB.rawQuery("SELECT * FROM " + table + " LIMIT 0,1",
                    null);
            if (checkUrlCursor.getColumnIndex("url") == -1) {
                sampleDB.execSQL("ALTER TABLE IF EXISTS" + table + " ADD COLUMN url VARCHAR;\n");
            }
        } catch (SQLiteException ignored) {
        }
    }

    public void removeTrack(final long dbId) {
        new Thread(new Runnable() {
            public void run() {
                Context context = LBApplication.getAppContext();
                SQLiteDatabase sampleDB = null;
                try {
                    sampleDB = context.openOrCreateDatabase(
                            DATABASE_TITLE, Context.MODE_PRIVATE, null);
                    sampleDB.beginTransaction();
                    sampleDB.delete(TRACKS_TABLE, "tid=" + dbId, null);
                    sampleDB.setTransactionSuccessful();
                } catch (SQLiteException e) {
                    // No operations.
                    e.printStackTrace();
                } finally {
                    if (sampleDB != null) {
                        sampleDB.endTransaction();
                        sampleDB.close();
                    }
                }
            }

        }).start();
    }

    public void addTrackToScrobble(final String artist, final String title, final String time) {
        new Thread(new Runnable() {
            public void run() {
                Context context = LBApplication.getAppContext();

                try {
                    String table = SCROBBLE_TABLE;
                    SQLiteDatabase sampleDB = null;
                    try {
                        sampleDB = context.openOrCreateDatabase(DATABASE_TITLE,
                                Context.MODE_PRIVATE, null);
                        sampleDB.beginTransaction();
                        sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " + table
                                + " (tid INTEGER PRIMARY KEY NOT NULL, artist VARCHAR, title VARCHAR, time INTEGER unique);");
                        sampleDB.setTransactionSuccessful();
                    } catch (SQLiteException e) {
                        // No operations.
                        e.printStackTrace();
                    } finally {
                        if (sampleDB != null) {
                            sampleDB.endTransaction();
                            sampleDB.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                SQLiteDatabase sampleDB = null;
                try {
                    sampleDB = context.openOrCreateDatabase(DATABASE_TITLE,
                            Context.MODE_PRIVATE, null);
                    sampleDB.beginTransaction();
                    ContentValues values = new ContentValues();
                    values.put("artist", artist);
                    values.put("title", title);
                    values.put("time", time);
                    sampleDB.insert(SCROBBLE_TABLE, null, values);
                    sampleDB.setTransactionSuccessful();
                } catch (SQLiteException e) {
                    // No operations.
                    e.printStackTrace();
                } finally {
                    if (sampleDB != null) {
                        sampleDB.endTransaction();
                        sampleDB.close();
                    }
                }
            }
        }).start();
    }

    public void addTrackToPlaylist(final Track track, final long pid) {
        new Thread(new Runnable() {
            public void run() {
                Context context = LBApplication.getAppContext();
                SQLiteDatabase sampleDB = null;
                try {
                    sampleDB = context.openOrCreateDatabase(
                            DATABASE_TITLE, Context.MODE_PRIVATE, null);
                    sampleDB.beginTransaction();
                    ContentValues values = new ContentValues();
                    values.put("artist", track.getArtist());
                    values.put("title", track.getTitle());
                    values.put("pid", pid);
                    String url = track.isLocal() ? track.getUrl() : "";
                    values.put("url", url);
                    sampleDB.insert(TRACKS_TABLE, null, values);
                    sampleDB.setTransactionSuccessful();
                } catch (SQLiteException e) {
                    // No operations.
                    e.printStackTrace();
                } finally {
                    if (sampleDB != null) {
                        sampleDB.endTransaction();
                        sampleDB.close();
                    }
                }
            }
        }).start();
    }

    public void updateDatabase() {
        SharedPreferences databasePreferences = SharedPreferencesManager.getDatabasePreferences();
        SharedPreferences.Editor editor = databasePreferences.edit();
        if (databasePreferences.getInt(Constants.DATABASE_VERSION, 0) < DATABASE_VERSION) {
            try {
                LBApplication.getAppContext().deleteDatabase(DATABASE_TITLE);
                editor.putInt(Constants.DATABASE_VERSION, DATABASE_VERSION);
                editor.apply();
            } catch (Exception ignored) {
            }
        }
    }

    public Track loadHeadTrackToScrobble() {
        Context context = LBApplication.getAppContext();
        SQLiteDatabase sampleDB = null;
        try {
            sampleDB = context.openOrCreateDatabase(DATABASE_TITLE,
                    Context.MODE_PRIVATE, null);
            sampleDB.beginTransaction();
            Cursor c = sampleDB.rawQuery("SELECT tid, artist, title, time FROM "
                    + SCROBBLE_TABLE, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    Long trackId = c.getLong(c.getColumnIndex("tid"));
                    String artist = c.getString(c.getColumnIndex("artist"));
                    String title = c.getString(c.getColumnIndex("title"));
                    Long time = c.getLong(c.getColumnIndex("time"));
                    return new Track(artist, title);
                }
            }
            sampleDB.setTransactionSuccessful();
        } catch (SQLiteException e) {
            // No operations.
        } finally {
            if (sampleDB != null) {
                try {
                    sampleDB.endTransaction();
                } catch (Exception ignored) {
                }
                sampleDB.close();
            }
        }
        return null;
    }

    public void removeHeadTrackToScrobble() {
        Context context = LBApplication.getAppContext();
        SQLiteDatabase sampleDB = null;
        try {
            sampleDB = context.openOrCreateDatabase(DATABASE_TITLE,
                    Context.MODE_PRIVATE, null);
            sampleDB.beginTransaction();
            Cursor c = sampleDB.rawQuery(String.format("SELECT [tid] FROM %s LIMIT 1",
                    SCROBBLE_TABLE), null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        long id = c.getLong(0);
                        sampleDB.delete(SCROBBLE_TABLE, "[tid] = " + id, null);
                    } while (c.moveToNext());
                }
            }
            sampleDB.setTransactionSuccessful();


        } catch (SQLiteException e) {
            // No operations.
        } finally {
            if (sampleDB != null) {
                try {
                    sampleDB.endTransaction();
                } catch (Exception ignored) {
                }
                sampleDB.close();
            }
        }
    }
}

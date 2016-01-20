package com.pillowapps.liqear.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.storio.DBPlaylist;
import com.pillowapps.liqear.entities.storio.DBPlaylistStorIOSQLiteDeleteResolver;
import com.pillowapps.liqear.entities.storio.DBPlaylistStorIOSQLiteGetResolver;
import com.pillowapps.liqear.entities.storio.DBPlaylistStorIOSQLitePutResolver;
import com.pillowapps.liqear.entities.storio.DBTrack;
import com.pillowapps.liqear.entities.storio.DBTrackStorIOSQLiteDeleteResolver;
import com.pillowapps.liqear.entities.storio.DBTrackStorIOSQLiteGetResolver;
import com.pillowapps.liqear.entities.storio.DBTrackStorIOSQLitePutResolver;
import com.pillowapps.liqear.entities.storio.PlaylistTable;
import com.pillowapps.liqear.entities.storio.TrackTable;
import com.pillowapps.liqear.helpers.db.DatabaseEntitiesMapper;
import com.pillowapps.liqear.helpers.db.LiquidBearSQLHelper;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import rx.Observable;

public class StorageManager {

    private static StorageManager INSTANCE;
    private final StorIOSQLite database;

    private StorageManager(Context context) {
        this.database = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new LiquidBearSQLHelper(context,
                        LiquidBearSQLHelper.DATABASE_NAME,
                        LiquidBearSQLHelper.DATABASE_VERSION))
                .addTypeMapping(DBPlaylist.class, SQLiteTypeMapping.<DBPlaylist>builder()
                        .putResolver(new DBPlaylistStorIOSQLitePutResolver())
                        .getResolver(new DBPlaylistStorIOSQLiteGetResolver())
                        .deleteResolver(new DBPlaylistStorIOSQLiteDeleteResolver())
                        .build())
                .addTypeMapping(DBTrack.class, SQLiteTypeMapping.<DBTrack>builder()
                        .putResolver(new DBTrackStorIOSQLitePutResolver())
                        .getResolver(new DBTrackStorIOSQLiteGetResolver())
                        .deleteResolver(new DBTrackStorIOSQLiteDeleteResolver())
                        .build())
                .build();
    }

    public static synchronized StorageManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new StorageManager(context);
        }
        return INSTANCE;
    }

    public Observable<DeleteResult> deleteMainPlaylist() {
        return database.get().listOfObjects(DBPlaylist.class)
                .withQuery(Query.builder()
                                .table(PlaylistTable.TABLE_NAME)
                                .where(String.format(Locale.getDefault(), "%s = ?", PlaylistTable.COLUMN_IS_MAIN_PLAYLIST))
                                .whereArgs(1)
                                .build()
                )
                .prepare()
                .createObservable()
                .flatMap(dbPlaylists -> {
                    HashSet<Long> playlistsToDelete = new HashSet<>(dbPlaylists.size());
                    for (DBPlaylist dbPlaylist : dbPlaylists) {
                        Long id = dbPlaylist.getId();
                        playlistsToDelete.add(id);
                    }
                    return Observable.from(playlistsToDelete);
                })
                .flatMap(this::deleteTracks);
    }

    public Observable<DeleteResult> deletePlaylist(Long id) {
        return database.get().object(DBPlaylist.class)
                .withQuery(Query.builder()
                                .table(PlaylistTable.TABLE_NAME)
                                .where(String.format(Locale.getDefault(), "%s = ? AND %s = ?",
                                        PlaylistTable.COLUMN_IS_MAIN_PLAYLIST,
                                        PlaylistTable.COLUMN_ID))
                                .whereArgs(0, id)
                                .build()
                )
                .prepare()
                .createObservable()
                .flatMap(dbPlaylist -> Observable.just(dbPlaylist.getId()))
                .flatMap(this::deleteTracks);
    }

    private Observable<DeleteResult> deleteTracks(Long playlistId) {
        return database.delete()
                .byQuery(DeleteQuery.builder()
                        .table(TrackTable.TABLE_NAME)
                        .whereArgs(String.format("%s = ?", TrackTable.COLUMN_PLAYLIST_ID))
                        .whereArgs(playlistId)
                        .build())
                .prepare()
                .createObservable();
    }

    @NonNull
    public Observable<Playlist> getMainPlaylist() {
        return database.get()
                .object(DBPlaylist.class)
                .withQuery(Query.builder()
                                .table(PlaylistTable.TABLE_NAME)
                                .where(String.format("%s = ?", PlaylistTable.COLUMN_IS_MAIN_PLAYLIST))
                                .whereArgs(1)
                                .build()
                ).prepare()
                .createObservable()
                .flatMap(dbPlaylist -> {
                    Playlist myPlaylist = DatabaseEntitiesMapper.map(dbPlaylist);
                    if (dbPlaylist == null) return Observable.just(myPlaylist);
                    List<DBTrack> dbTracks = database.get().listOfObjects(DBTrack.class)
                            .withQuery(Query.builder().table(TrackTable.TABLE_NAME)
                                    .where(String.format(Locale.getDefault(), "%s = ?", TrackTable.COLUMN_PLAYLIST_ID))
                                    .whereArgs(myPlaylist.getId())
                                    .build()).prepare().executeAsBlocking();
                    List<Track> tracks = DatabaseEntitiesMapper.mapListOfDBTracks(dbTracks);
                    myPlaylist.setTracks(tracks);

                    return Observable.just(myPlaylist);
                });
    }

    @NonNull
    public Observable<List<Playlist>> getPlaylists() {
        return database.get()
                .listOfObjects(DBPlaylist.class)
                .withQuery(Query.builder()
                                .table(PlaylistTable.TABLE_NAME)
                                .where(String.format("%s = ?", PlaylistTable.COLUMN_IS_MAIN_PLAYLIST))
                                .whereArgs(0)
                                .build()
                ).prepare()
                .createObservable()
                .flatMap(dbPlaylists -> {
                    List<Playlist> playlists = DatabaseEntitiesMapper.mapListOfDBPlaylists(dbPlaylists);
                    if (dbPlaylists == null) return Observable.empty();
                    for (Playlist playlist : playlists) {
                        List<DBTrack> dbTracks = database.get().listOfObjects(DBTrack.class)
                                .withQuery(Query.builder().table(TrackTable.TABLE_NAME)
                                        .where(String.format(Locale.getDefault(), "%s = ?", TrackTable.COLUMN_PLAYLIST_ID))
                                        .whereArgs(playlist.getId())
                                        .build()).prepare().executeAsBlocking();
                        List<Track> tracks = DatabaseEntitiesMapper.mapListOfDBTracks(dbTracks);
                        playlist.setTracks(tracks);
                    }

                    return Observable.just(playlists);
                });
    }

    public Observable<PutResult> savePlaylist(Playlist playlist) {
        return database.put()
                .object(DatabaseEntitiesMapper.map(playlist))
                .prepare()
                .createObservable();
    }

    public Observable<PutResult> renamePlaylist(Long id, String newTitle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlaylistTable.COLUMN_TITLE, newTitle);
        return database.put()
                .contentValues(contentValues)
                .withPutResolver(new DefaultPutResolver<ContentValues>() {
                    @NonNull
                    @Override
                    protected InsertQuery mapToInsertQuery(@NonNull ContentValues object) {
                        return InsertQuery.builder()
                                .table(PlaylistTable.TABLE_NAME)
                                .build();
                    }

                    @NonNull
                    @Override
                    protected UpdateQuery mapToUpdateQuery(@NonNull ContentValues object) {
                        return UpdateQuery.builder()
                                .table(PlaylistTable.TABLE_NAME)
                                .where(PlaylistTable.COLUMN_ID + " = ?")
                                .whereArgs(id)
                                .build();
                    }

                    @NonNull
                    @Override
                    protected ContentValues mapToContentValues(@NonNull ContentValues object) {
                        return object;
                    }
                }).prepare().createObservable();
    }

    public Observable<PutResult> saveTrackToPlaylist(Long playlistId, Track track) {
        DBTrack dbTrack = DatabaseEntitiesMapper.map(track);
        dbTrack.setPlaylistId(playlistId);

        return database.put()
                .object(dbTrack)
                .prepare()
                .createObservable();
    }

    public Observable<Playlist> getPlaylist(Long playlistId) {
        return database.get()
                .object(DBPlaylist.class)
                .withQuery(Query.builder()
                                .table(PlaylistTable.TABLE_NAME)
                                .where(String.format("%s = ?", PlaylistTable.COLUMN_ID))
                                .whereArgs(playlistId)
                                .build()
                ).prepare()
                .createObservable()
                .flatMap(dbPlaylist -> {
                    Playlist playlist = DatabaseEntitiesMapper.map(dbPlaylist);
                    if (dbPlaylist == null) return Observable.empty();
                    List<DBTrack> dbTracks = database.get().listOfObjects(DBTrack.class)
                            .withQuery(Query.builder().table(TrackTable.TABLE_NAME)
                                    .where(String.format(Locale.getDefault(), "%s = ?", TrackTable.COLUMN_PLAYLIST_ID))
                                    .whereArgs(playlist.getId())
                                    .build()).prepare().executeAsBlocking();
                    List<Track> tracks = DatabaseEntitiesMapper.mapListOfDBTracks(dbTracks);
                    playlist.setTracks(tracks);

                    return Observable.just(playlist);
                });
    }
}

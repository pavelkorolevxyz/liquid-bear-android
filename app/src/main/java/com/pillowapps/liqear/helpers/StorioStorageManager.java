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

import java.util.List;
import java.util.Locale;

import rx.Observable;

public class StorioStorageManager implements PlaylistsStorage {

    private final StorIOSQLite database;

    public StorioStorageManager(Context context) {
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

    @Override
    public Observable<DeleteResult> findAndDeleteMainPlaylist() {
        return deletePlaylistWithTracks(database.get()
                .object(DBPlaylist.class)
                .withQuery(Query.builder()
                        .table(PlaylistTable.TABLE_NAME)
                        .where(String.format(Locale.getDefault(), "%s = ?",
                                PlaylistTable.COLUMN_IS_MAIN_PLAYLIST))
                        .whereArgs(1)
                        .build()
                )
                .prepare()
                .asRxObservable()
        );
    }

    @Override
    public Observable<DeleteResult> findAndDeletePlaylist(@NonNull Long playlistId) {
        return deletePlaylistWithTracks(database.get().object(DBPlaylist.class)
                .withQuery(Query.builder()
                        .table(PlaylistTable.TABLE_NAME)
                        .where(String.format(Locale.getDefault(), "%s = ?",
                                PlaylistTable.COLUMN_ID))
                        .whereArgs(playlistId)
                        .build()
                )
                .prepare()
                .asRxObservable()
        );
    }

    private Observable<DeleteResult> deletePlaylistWithTracks(Observable<DBPlaylist> playlistObservable) {
        return playlistObservable
                .flatMap(dbPlaylist -> {
                            Long playlistId = dbPlaylist.getId();
                            if (playlistId == null) {
                                return Observable.empty();
                            }
                            return deleteTracksFromPlaylist(playlistId)
                                    .map(deleteResult -> dbPlaylist);
                        }
                ).flatMap(dbPlaylist -> database.delete()
                        .object(dbPlaylist)
                        .prepare()
                        .asRxObservable()
                ).take(1);
    }

    private Observable<DeleteResult> deleteTracksFromPlaylist(@NonNull Long playlistId) {
        return database.delete()
                .byQuery(DeleteQuery.builder()
                        .table(TrackTable.TABLE_NAME)
                        .where(String.format("%s = ?", TrackTable.COLUMN_PLAYLIST_ID))
                        .whereArgs(playlistId)
                        .build())
                .prepare()
                .asRxObservable();
    }

    @NonNull
    @Override
    public Observable<Playlist> getMainPlaylist() {
        return database.get()
                .object(DBPlaylist.class)
                .withQuery(Query.builder()
                        .table(PlaylistTable.TABLE_NAME)
                        .where(String.format("%s = ?", PlaylistTable.COLUMN_IS_MAIN_PLAYLIST))
                        .whereArgs(1)
                        .build()
                ).prepare()
                .asRxObservable()
                .take(1)
                .flatMap(dbPlaylist -> {
                    Playlist myPlaylist = DatabaseEntitiesMapper.map(dbPlaylist);
                    if (dbPlaylist == null || dbPlaylist.getId() == null) {
                        return Observable.just(myPlaylist);
                    }
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
    @Override
    public Observable<List<Playlist>> getPlaylists() {
        return database.get()
                .listOfObjects(DBPlaylist.class)
                .withQuery(Query.builder()
                        .table(PlaylistTable.TABLE_NAME)
                        .where(String.format("%s = ?", PlaylistTable.COLUMN_IS_MAIN_PLAYLIST))
                        .whereArgs(0)
                        .build()
                ).prepare()
                .asRxObservable()
                .take(1)
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

    @Override
    public Observable<Long> savePlaylist(Playlist playlist) {
        return database.put()
                .object(DatabaseEntitiesMapper.map(playlist))
                .prepare()
                .asRxObservable()
                .map(PutResult::insertedId)
                .flatMap(playlistId -> saveTracksToPlaylist(playlistId, playlist.getTracks()));
    }

    @Override
    public Observable<Long> saveTracksToPlaylist(@NonNull Long playlistId, List<Track> tracks) {
        return Observable.from(tracks)
                .map(DatabaseEntitiesMapper::map)
                .map(dbTrack -> {
                    dbTrack.setPlaylistId(playlistId);
                    return dbTrack;
                })
                .toList()
                .flatMap(dbTracks -> database.put()
                        .objects(dbTracks)
                        .prepare()
                        .asRxObservable())
                .map(deleteResult -> playlistId);
    }

    @Override
    public Observable<PutResult> renamePlaylist(@NonNull Long playlistId, String newTitle) {
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
                                .whereArgs(playlistId)
                                .build();
                    }

                    @NonNull
                    @Override
                    protected ContentValues mapToContentValues(@NonNull ContentValues object) {
                        return object;
                    }
                }).prepare().asRxObservable();
    }

    @Override
    public Observable<PutResult> saveTrackToPlaylist(@NonNull Long playlistId, Track track) {
        DBTrack dbTrack = DatabaseEntitiesMapper.map(track);
        dbTrack.setPlaylistId(playlistId);

        return database.put()
                .object(dbTrack)
                .prepare()
                .asRxObservable();
    }

    @Override
    public Observable<Playlist> getPlaylist(@NonNull Long playlistId) {
        return database.get()
                .object(DBPlaylist.class)
                .withQuery(Query.builder()
                        .table(PlaylistTable.TABLE_NAME)
                        .where(String.format("%s = ?", PlaylistTable.COLUMN_ID))
                        .whereArgs(playlistId)
                        .build()
                ).prepare()
                .asRxObservable()
                .take(1)
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

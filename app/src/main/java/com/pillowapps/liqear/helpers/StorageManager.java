package com.pillowapps.liqear.helpers;

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
import com.pillowapps.liqear.helpers.db.LiquidBearSQLHelper;
import com.pillowapps.liqear.helpers.db.StorioEntitiesMapper;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.List;
import java.util.Locale;

import rx.Observable;

public class StorageManager {

    private static StorageManager INSTANCE;
    private final StorIOSQLite database;

    private StorageManager(Context context) {
        this.database = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new LiquidBearSQLHelper(context, "LB", 4))
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

    @NonNull
    public Observable<DeleteResult> deleteMainPlaylist() {
        return database.delete().byQuery(DeleteQuery.builder()
                        .table(PlaylistTable.TABLE_NAME)
                        .where(String.format(Locale.getDefault(), "%s=?", PlaylistTable.COLUMN_IS_MAIN_PLAYLIST))
                        .whereArgs(true)
                        .build()
        ).prepare().createObservable();
        //todo delete tracks from main playlist too
    }

    @NonNull
    public Observable<Playlist> getMainPlaylist() {
        return database.get()
                .object(DBPlaylist.class)
                .withQuery(Query.builder()
                                .table(PlaylistTable.TABLE_NAME)
                                .where(String.format("%s=?", PlaylistTable.COLUMN_IS_MAIN_PLAYLIST))
                                .whereArgs(1)
                                .build()
                ).prepare()
                .createObservable()
                .flatMap(dbPlaylist -> {
                    Playlist myPlaylist = StorioEntitiesMapper.map(dbPlaylist);
                    if (dbPlaylist == null) return Observable.just(myPlaylist);
                    List<DBTrack> dbTracks = database.get().listOfObjects(DBTrack.class)
                            .withQuery(Query.builder().table(TrackTable.TABLE_NAME)
                                    .where(String.format(Locale.getDefault(), "%s=?", TrackTable.COLUMN_PLAYLIST_ID))
                                    .whereArgs(dbPlaylist.getId())
                                    .build()).prepare().executeAsBlocking(); // todo make chain of observables instead
                    List<Track> tracks = StorioEntitiesMapper.mapListOfDBTracks(dbTracks);
                    myPlaylist.setTracks(tracks);

                    return Observable.just(myPlaylist);
                });
    }

    public Observable<PutResult> savePlaylist(Playlist playlist) {
        return database.put()
                .object(StorioEntitiesMapper.map(playlist))
                .prepare()
                .createObservable();
    }
}

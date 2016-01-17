package com.pillowapps.liqear.helpers.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pillowapps.liqear.entities.storio.PlaylistTable;
import com.pillowapps.liqear.entities.storio.TrackTable;

public class LiquidBearSQLHelper extends SQLiteOpenHelper {

    public LiquidBearSQLHelper(@NonNull Context context, @NonNull String dataBaseName,
                               int dataBaseVersion) {
        super(context, dataBaseName, null, dataBaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                PlaylistTable.TABLE_NAME + "(" +
                PlaylistTable.COLUMN_ID + " INTEGER PRIMARY KEY UNIQUE," +
                PlaylistTable.COLUMN_TITLE + " TEXT, " +
                PlaylistTable.COLUMN_IS_MAIN_PLAYLIST + " INTEGER DEFAULT 0);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                TrackTable.TABLE_NAME + "(" +
                TrackTable.COLUMN_ID + "INTEGER PRIMARY KEY UNIQUE," +
                TrackTable.COLUMN_PLAYLIST_ID + " INTEGER," +
                TrackTable.COLUMN_TITLE + " TEXT, " +
                TrackTable.COLUMN_ARTIST + " TEXT, " +
                TrackTable.COLUMN_ALBUM + " TEXT, " +
                TrackTable.COLUMN_OWNER_ID + " INTEGER, " +
                TrackTable.COLUMN_AUDIO_ID + " INTEGER, " +
                TrackTable.COLUMN_IS_LOCAL + " INTEGER DEFAULT 0," +
                TrackTable.COLUMN_LOCAL_URL + " TEXT, " +
                "FOREIGN KEY(" + TrackTable.COLUMN_PLAYLIST_ID + ") REFERENCES " +
                PlaylistTable.TABLE_NAME + "(" + PlaylistTable.COLUMN_ID + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // todo support of LB 2.0 Database
    }
}

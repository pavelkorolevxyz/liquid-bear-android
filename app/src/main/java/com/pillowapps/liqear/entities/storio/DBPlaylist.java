package com.pillowapps.liqear.entities.storio;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

@StorIOSQLiteType(table = PlaylistTable.TABLE_NAME)
public class DBPlaylist {

    @StorIOSQLiteColumn(name = PlaylistTable.COLUMN_ID, key = true)
    Long id;

    @StorIOSQLiteColumn(name = PlaylistTable.COLUMN_TITLE)
    String title;

    @StorIOSQLiteColumn(name = PlaylistTable.COLUMN_IS_MAIN_PLAYLIST)
    boolean mainPlaylist = false;

    public DBPlaylist() {
    }

    public DBPlaylist(Long id, String title, boolean mainPlaylist) {
        this.id = id;
        this.title = title;
        this.mainPlaylist = mainPlaylist;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isMainPlaylist() {
        return mainPlaylist;
    }
}

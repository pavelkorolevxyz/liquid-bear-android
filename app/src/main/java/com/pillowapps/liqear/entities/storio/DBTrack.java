package com.pillowapps.liqear.entities.storio;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

@StorIOSQLiteType(table = TrackTable.TABLE_NAME)
public class DBTrack {

    @StorIOSQLiteColumn(name = TrackTable.COLUMN_ID, key = true)
    Long id;

    @StorIOSQLiteColumn(name = TrackTable.COLUMN_PLAYLIST_ID)
    Long playlistId;

    @StorIOSQLiteColumn(name = TrackTable.COLUMN_TITLE)
    String title;
    @StorIOSQLiteColumn(name = TrackTable.COLUMN_ARTIST)
    String artist;
    @StorIOSQLiteColumn(name = TrackTable.COLUMN_ALBUM)
    String album;

    @StorIOSQLiteColumn(name = TrackTable.COLUMN_OWNER_ID)
    Long ownerId;
    @StorIOSQLiteColumn(name = TrackTable.COLUMN_AUDIO_ID)
    Long audioId;

    @StorIOSQLiteColumn(name = TrackTable.COLUMN_IS_LOCAL)
    Boolean local;
    @StorIOSQLiteColumn(name = TrackTable.COLUMN_LOCAL_URL)
    String localUrl;

    public DBTrack() {
    }

    public DBTrack(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Long playlistId) {
        this.playlistId = playlistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getAudioId() {
        return audioId;
    }

    public void setAudioId(Long audioId) {
        this.audioId = audioId;
    }

    public Boolean getLocal() {
        return local;
    }

    public void setLocal(Boolean local) {
        this.local = local;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }
}

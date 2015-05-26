package com.pillowapps.liqear.entities;

import com.pillowapps.liqear.helpers.LBDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

@ModelContainer
@Table(databaseName = LBDatabase.NAME)
public class Playlist extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    Long id;
    @Column
    String title;
    @Column
    boolean mainPlaylist = false;

    List<Track> tracks;

    public Playlist(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Playlist() {
        // No operations.
    }

    public Playlist(List<Track> tracks) {
        this.tracks = tracks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long _id) {
        this.id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @OneToMany(methods = {OneToMany.Method.ALL})
    public List<Track> getTracks() {
        if (tracks == null) {
            tracks = new Select().from(Track.class).where(Condition.column("playlistId").is(id)).queryList();
        }
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public void setMainPlaylist(boolean mainPlaylist) {
        this.mainPlaylist = mainPlaylist;
    }

    @Override
    public void save() {
        super.save();
        for (Track track : tracks) {
            track.setPlaylistId(id);
            track.save();
        }
    }

    @Override
    public void delete() {
        super.delete();
        new Delete().from(Track.class).where(Condition.column("playlistId").is(id));
    }
}

package com.pillowapps.liqear.entities;

import com.pillowapps.liqear.helpers.db.LiquidBearDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

@ModelContainer
@Table(databaseName = LiquidBearDatabase.NAME)
public class Playlist extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    public Long id;

    @Column
    public String title;

    @Column
    public boolean mainPlaylist = false;

    List<Track> tracks;

    public Playlist(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Playlist() {
        // No operations.
    }

    @Override
    public void save() {
        super.save();
        for (Track track : tracks) {
            track.associatePlaylist(this);
            track.save();
        }
    }

    @Override
    public void delete() {
        for (Track track : getTracks()) {
            track.delete();
        }
        super.delete();
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

//    public List<Track> getTracks() {
//        return tracks;
//    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public void setMainPlaylist(boolean mainPlaylist) {
        this.mainPlaylist = mainPlaylist;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "tracks")
    public List<Track> getTracks() {
        if (tracks == null) {
            tracks = new Select()
                    .from(Track.class)
                    .where(Condition.column(Track$Table.PLAYLISTMODELCONTAINER_PLAYLISTID).is(id))
                    .queryList();
        }
        return tracks;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", mainPlaylist=" + mainPlaylist +
                ", tracks=" + tracks +
                '}';
    }
}

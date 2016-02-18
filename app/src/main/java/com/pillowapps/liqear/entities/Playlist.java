package com.pillowapps.liqear.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Playlist implements Parcelable {

    private Long id;

    private String title;

    private List<Track> tracks = new ArrayList<>();

    private Boolean mainPlaylist = false;

    public Playlist(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Playlist(String title, List<Track> tracks) {
        this.title = title;
        this.tracks = tracks;
    }

    public Playlist(@NonNull List<Track> tracks) {
        this.tracks = tracks;
    }

    public Playlist() {
        // No operations.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public Boolean isMainPlaylist() {
        return mainPlaylist;
    }

    public void setMainPlaylist(Boolean mainPlaylist) {
        this.mainPlaylist = mainPlaylist;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "title='" + title + '\'' +
                ", tracks=" + tracks +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.title);
        dest.writeTypedList(tracks);
        dest.writeValue(this.mainPlaylist);
    }

    protected Playlist(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.title = in.readString();
        this.tracks = in.createTypedArrayList(Track.CREATOR);
        this.mainPlaylist = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>() {
        public Playlist createFromParcel(Parcel source) {
            return new Playlist(source);
        }

        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };
}

package com.pillowapps.liqear.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Album implements Parcelable {

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
    private List<String> tracks;
    private String artist;
    private String title;
    private String imageUrl;
    private String genre = "";
    private long albumId;
    private long listeners;
    private long playcount;
    private String id;
    private String publishDate;

    public Album(String artist, String title, long albumId) {
        this.artist = artist;
        this.title = title;
        this.albumId = albumId;
        tracks = new ArrayList<String>();
    }

    public Album(String artist, String title) {
        this.artist = artist;
        this.title = title;
        tracks = new ArrayList<String>();
    }

    public Album(String artist, String title, String genre) {
        this.artist = artist;
        this.title = title;
        this.genre = genre;
        tracks = new ArrayList<String>();
    }

    public Album(String artist, String title, String genre, String albumId, String urlImage) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        tracks = new ArrayList<String>();
        this.id = albumId;
        this.imageUrl = urlImage;
    }


    private Album(Parcel parcel) {
        title = parcel.readString();
        artist = parcel.readString();
        imageUrl = parcel.readString();
        tracks = new ArrayList<String>();
        parcel.readStringList(tracks);
        albumId = parcel.readLong();
        genre = parcel.readString();
        id = parcel.readString();
        publishDate = parcel.readString();
    }

    public Album() {
        tracks = new ArrayList<String>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Album{" +
                "tracks=" + tracks +
                ", artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", genre='" + genre + '\'' +
                ", albumId=" + albumId +
                ", id='" + id + '\'' +
                '}';
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        if (publishDate != null) {
            int end = publishDate.indexOf(",");
            if (end == -1) end = publishDate.length();
            this.publishDate = publishDate.substring(0, end).trim();
        }
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

    public List<String> getTracks() {
        return tracks;
    }

    public void setTracks(List<String> tracks) {
        this.tracks = tracks;
    }

    public void add(String track) {
        tracks.add(track);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNotation() {
        if (artist == null) {
            return title;
        }
        return artist + " - " + title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getListeners() {
        return listeners;
    }

    public void setListeners(long listeners) {
        this.listeners = listeners;
    }

    public long getPlaycount() {
        return playcount;
    }

    public void setPlaycount(long playcount) {
        this.playcount = playcount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (artist == null || title == null) return false;

        Album album = (Album) o;

        if (!artist.equals(album.artist)) return false;
        if (!title.equals(album.title)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = artist.hashCode();
        result = 31 * result + title.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(imageUrl);
        parcel.writeStringList(tracks);
        parcel.writeLong(albumId);
        parcel.writeString(genre);
        parcel.writeString(id);
        parcel.writeString(publishDate);
    }
}

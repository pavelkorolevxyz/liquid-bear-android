package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmAlbumWithName {
    @SerializedName("title")
    private String title;
    @SerializedName("artist")
    private String artistName;
    @SerializedName("releaseDate")
    private String releaseDate;
    @SerializedName("image")
    private List<LastfmImage> images;
    @SerializedName("tracks")
    private LastfmTracks tracks;

    public LastfmAlbumWithName() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setImages(List<LastfmImage> images) {
        this.images = images;
    }

    public void setTracks(LastfmTracks tracks) {
        this.tracks = tracks;
    }

    public String getTitle() {
        return title;
    }

    public String getArtistName() {
        return artistName;
    }

    public List<LastfmImage> getImages() {
        return images;
    }

    public LastfmTracks getTracks() {
        return tracks;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String toString() {
        return "LastfmAlbumWithName{" +
                "title='" + title + '\'' +
                ", artistName='" + artistName + '\'' +
                ", releaseDate=" + releaseDate +
                ", images=" + images +
                ", tracks=" + tracks +
                '}';
    }
}

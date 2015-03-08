package com.pillowapps.liqear.entities.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmTrack {

    @SerializedName("name")
    private String name;
    @SerializedName("artist")
    private LastfmArtist artist;
    @SerializedName("album")
    private LastfmAlbum album;
    @SerializedName("userloved")
    private Boolean loved;

    public LastfmTrack() {
    }

    public String getName() {
        return name;
    }

    public LastfmArtist getArtist() {
        return artist;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(LastfmArtist artist) {
        this.artist = artist;
    }

    public LastfmAlbum getAlbum() {
        return album;
    }

    public Boolean isLoved() {
        return loved;
    }

    @Override
    public String toString() {
        return "LastfmTrack{" +
                "name='" + name + '\'' +
                ", artist=" + artist +
                ", album=" + album +
                ", loved=" + loved +
                '}';
    }
}


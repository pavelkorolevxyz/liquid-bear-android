package com.pillowapps.liqear.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.pillowapps.liqear.helpers.TrackUtils;

public class Track implements Parcelable {

    public String title;
    public String artist;
    public String album;

    public long ownerId;
    public long audioId;

    public boolean local;
    public String localUrl;

    private String url;

    public int duration;

    private int realPosition;
    private boolean loved = false;
    private boolean addedToVk = false;

    public Track(long audioId, long oid) {
        this.audioId = audioId;
        this.ownerId = oid;
    }

    public Track(String artist, String title, String localUrl, boolean local) {
        this.artist = artist;
        this.title = title;
        this.localUrl = localUrl;
        this.local = local;
    }

    public Track(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    public Track() {
    }

    public Track(String artist, String title, String url) {
        this.artist = artist;
        this.title = title;
        this.url = url;
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

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getAudioId() {
        return audioId;
    }

    public void setAudioId(long audioId) {
        this.audioId = audioId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLoved() {
        return loved;
    }

    public void setLoved(boolean loved) {
        this.loved = loved;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isAddedToVk() {
        return addedToVk;
    }

    public void setAddedToVk(boolean addedToVk) {
        this.addedToVk = addedToVk;
    }

    public int getRealPosition() {
        return realPosition;
    }

    public void setRealPosition(int realPosition) {
        this.realPosition = realPosition;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Track{" + TrackUtils.getNotation(this) + "}";
    }

    public String getLocalUrl() {
        return localUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.artist);
        dest.writeString(this.album);
        dest.writeLong(this.ownerId);
        dest.writeLong(this.audioId);
        dest.writeByte(local ? (byte) 1 : (byte) 0);
        dest.writeString(this.localUrl);
        dest.writeString(this.url);
        dest.writeInt(this.duration);
        dest.writeInt(this.realPosition);
        dest.writeByte(loved ? (byte) 1 : (byte) 0);
        dest.writeByte(addedToVk ? (byte) 1 : (byte) 0);
    }

    protected Track(Parcel in) {
        this.title = in.readString();
        this.artist = in.readString();
        this.album = in.readString();
        this.ownerId = in.readLong();
        this.audioId = in.readLong();
        this.local = in.readByte() != 0;
        this.localUrl = in.readString();
        this.url = in.readString();
        this.duration = in.readInt();
        this.realPosition = in.readInt();
        this.loved = in.readByte() != 0;
        this.addedToVk = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}

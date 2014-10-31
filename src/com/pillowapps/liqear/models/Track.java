package com.pillowapps.liqear.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Track implements Parcelable {
    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
    private String title;
    private String artist;
    private int duration;
    private int percentageChange;
    private int loves;
    private int listeners;
    private long playCount;
    private long userPlayCount;
    private long ownerId;
    private long aid;
    private String url = null;
    private boolean loved = false;
    private boolean live = false;
    private String ip;
    private int realPosition;
    private boolean current = false;
    private long dbId;
    private boolean local = false;
    private long scrobbleTime;
    private boolean addedToVk = false;

    public Track(long aid, long oid) {
        this.aid = aid;
        this.ownerId = oid;
    }

    public Track(String artist, String title, String url, boolean local) {
        this.artist = artist;
        this.title = title;
        this.url = url;
        this.local = local;
    }

    public Track(String artist, String title, Long dbId, String url) {
        this.artist = artist;
        this.title = title;
        this.dbId = dbId;
        this.url = url;
    }

    public Track(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    public Track(String artist, String title, boolean live) {
        this.artist = artist;
        this.title = title;
        this.live = live;
    }

    public Track() {
    }

    public Track(String artist, String title, String url) {
        this.artist = artist;
        this.title = title;
        this.url = url;
    }

    public Track(String artist, String title, long dbId, long scrobbleTime) {
        this.artist = artist;
        this.title = title;
        this.dbId = dbId;
        this.scrobbleTime = scrobbleTime;
    }

    private Track(Parcel parcel) {
        title = parcel.readString();
        artist = parcel.readString();
        duration = parcel.readInt();
        percentageChange = parcel.readInt();
        loves = parcel.readInt();
        listeners = parcel.readInt();
        playCount = parcel.readLong();
        userPlayCount = parcel.readLong();
        ownerId = parcel.readLong();
        aid = parcel.readLong();
        url = parcel.readString();
        loved = parcel.readByte() == 1;
        live = parcel.readByte() == 1;
        current = parcel.readByte() == 1;
        local = parcel.readByte() == 1;
        ip = parcel.readString();
        realPosition = parcel.readInt();
        dbId = parcel.readLong();
        scrobbleTime = parcel.readLong();

    }

    public Long getScrobbleTime() {
        return scrobbleTime;
    }

    public void setScrobbleTime(Long scrobbleTime) {
        this.scrobbleTime = scrobbleTime;
    }

    public int getRealPosition() {
        return realPosition;
    }

    public void setRealPosition(int realPosition) {
        this.realPosition = realPosition;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    @Override
    public String toString() {
        return "Track{" +
                getNotation() +
                "}";
    }

    public String getNotation() {
        return artist + " - " + title;
    }

    public int getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(int percentageChange) {
        this.percentageChange = percentageChange;
    }

    public int getLoves() {
        return loves;
    }

    public void setLoves(int loves) {
        this.loves = loves;
    }

    public int getListeners() {
        return listeners;
    }

    public void setListeners(int listeners) {
        this.listeners = listeners;
    }

    public long getPlayCount() {
        return playCount;
    }

    public void setPlayCount(long playCount) {
        this.playCount = playCount;
    }

    public long getUserPlayCount() {
        return userPlayCount;
    }

    public void setUserPlayCount(long userPlayCount) {
        this.userPlayCount = userPlayCount;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getAid() {
        return aid;
    }

    public void setAid(long aid) {
        this.aid = aid;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDurationString() {
        SimpleDateFormat format = new SimpleDateFormat("m:ss");
        return format.format(new Date(duration * 1000));
    }

    public boolean isLoved() {
        return loved;
    }

    public void setLoved(boolean loved) {
        this.loved = loved;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    public boolean isLive() {
        return live;
    }

    public boolean isLocal() {
        return local;
    }

    private String trim(String s) {
        return s.replaceAll("[^\\w\\d\\[\\]\\(\\)\\{\\}\\s/]", "").toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object) this).getClass() != o.getClass()) return false;

        Track track = (Track) o;

        return artist.equals(track.artist) && title.equals(track.title);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + artist.hashCode();
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
        parcel.writeInt(duration);
        parcel.writeInt(percentageChange);
        parcel.writeInt(loves);
        parcel.writeInt(listeners);
        parcel.writeLong(playCount);
        parcel.writeLong(userPlayCount);
        parcel.writeLong(ownerId);
        parcel.writeLong(aid);
        parcel.writeString(url);
        parcel.writeByte((byte) (loved ? 1 : 0));
        parcel.writeByte((byte) (live ? 1 : 0));
        parcel.writeByte((byte) (current ? 1 : 0));
        parcel.writeByte((byte) (local ? 1 : 0));
        parcel.writeString(ip);
        parcel.writeInt(realPosition);
        parcel.writeLong(dbId);
        parcel.writeLong(scrobbleTime);
    }

    public void setAddedToVk(boolean addedToVk) {
        this.addedToVk = addedToVk;
    }

    public boolean isAddedToVk() {
        return addedToVk;
    }
}

package com.pillowapps.liqear.entities;


import android.os.Parcel;
import android.os.Parcelable;

public class Group implements Parcelable {
    private String name;
    private long gid;
    private String imageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getGid() {
        return gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.gid);
        dest.writeString(this.imageUrl);
    }

    public Group() {
    }

    protected Group(Parcel in) {
        this.name = in.readString();
        this.gid = in.readLong();
        this.imageUrl = in.readString();
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}

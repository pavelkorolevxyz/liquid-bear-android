package com.pillowapps.liqear.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class User implements Parcelable {

    @SerializedName("name")
    private String name = "";
    private long uid;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public User(String name) {
        super();
        this.name = name;
    }

    public User(String name, long uid) {
        this.name = name;
        this.uid = uid;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.uid);
        dest.writeString(this.imageUrl);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.uid = in.readLong();
        this.imageUrl = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}

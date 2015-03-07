package com.pillowapps.liqear.entities;

import com.google.gson.annotations.SerializedName;

public class Session {
    @SerializedName("name")
    private String name;
    @SerializedName("key")
    private String key;

    public Session() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Session{" +
                "name='" + name + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}

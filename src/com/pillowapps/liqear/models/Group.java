package com.pillowapps.liqear.models;

import java.io.Serializable;

public class Group implements Serializable{
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
}

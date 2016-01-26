package com.pillowapps.liqear.entities;

public class Artist {
    private String name;
    private String previewUrl;
    private String id;

    public Artist(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Artist(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        return name.equals(artist.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

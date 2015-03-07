package com.pillowapps.liqear.entities;

public class Artist {
    private String name;
    private Image[] images;
    private String previewUrl;
    private int percentagechange;
    private long playcount;
    private int listeners;
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

    @Override
    public String toString() {
        return "Artist [name=" + name + ", previewUrl=" + previewUrl
                + ", percentagechange=" + percentagechange + ", playcount="
                + playcount + ", listeners=" + listeners + "]";
    }

    public Artist(String name) {
        super();
        this.name = name;
    }

    public Artist(String name, int percentagechange) {
        super();
        this.name = name;
        this.percentagechange = percentagechange;
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

    public int getPercentagechange() {
        return percentagechange;
    }

    public int getListeners() {
        return listeners;
    }

    public long getPlaycount() {
        return playcount;
    }

    public void setListeners(int listeners) {
        this.listeners = listeners;
    }

    public void setPlaycount(long playcount) {
        this.playcount = playcount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPercentagechange(int percentagechange) {
        this.percentagechange = percentagechange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        if (!name.equals(artist.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

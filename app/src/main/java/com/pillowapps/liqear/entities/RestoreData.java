package com.pillowapps.liqear.entities;

public class RestoreData {
    private final String artist;
    private final String title;
    private final int currentIndex;
    private final int position;

    public RestoreData(String artist, String title, int currentIndex, int position) {
        this.artist = artist;
        this.title = title;
        this.currentIndex = currentIndex;
        this.position = position;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getPosition() {
        return position;
    }
}

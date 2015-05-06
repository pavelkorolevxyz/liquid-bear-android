package com.pillowapps.liqear.entities.events;

public class ArtistInfoEvent {
    private String imageUrl;

    public ArtistInfoEvent(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

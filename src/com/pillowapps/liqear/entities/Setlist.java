package com.pillowapps.liqear.entities;

import java.util.List;

public class Setlist {
    private String artist;
    private List<String> tracks;
    private String city;
    private String country;
    private String date;

    public String getVenue() {
        return venue;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public List<String> getTracks() {
        return tracks;
    }

    private String venue;

    public String getArtist() {
        return artist;
    }

    public Setlist(String artist, List<String> tracks, String city,
                   String country, String venue, String date) {
        this.artist = artist;
        this.tracks = tracks;
        this.city = city;
        this.country = country;
        this.venue = venue;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Setlist{" +
                "artist='" + artist + '\'' +
                ", tracks=" + tracks +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", venue='" + venue + '\'' +
                '}';
    }

    public String getNotation() {
        return artist + " [" + getConcert() + "]";
    }

    public String getConcert() {
        return city + ", " + country + " / " + venue + " " + date;
    }


}
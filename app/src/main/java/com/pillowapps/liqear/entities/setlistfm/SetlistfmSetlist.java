package com.pillowapps.liqear.entities.setlistfm;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.helpers.DateUtils;

import java.util.Date;

public class SetlistfmSetlist {
    @SerializedName("sets")
    private SetlistfmSets sets;
    @SerializedName("artist")
    private SetlistfmArtist artist;
    @SerializedName("venue")
    private SetlistfmVenue venue;
    @SerializedName("@eventDate")
    private Date eventDate;

    public SetlistfmSetlist() {
    }

    public SetlistfmSets getSets() {
        return sets;
    }

    public SetlistfmArtist getArtist() {
        return artist;
    }

    public SetlistfmVenue getVenue() {
        return venue;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public String getNotation() {
        return artist.getName() + " [" + getConcert() + "]";
    }

    public String getConcert() {
        SetlistfmCity city = venue.getCity();
        SetlistfmCountry country = city.getCountry();
        return String.format("%s, %s / %s %s",
                city.getName(),
                country.getName(),
                venue.getName(),
                DateUtils.formatDate(eventDate));
    }
}

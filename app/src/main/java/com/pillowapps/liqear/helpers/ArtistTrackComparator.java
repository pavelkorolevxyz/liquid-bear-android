package com.pillowapps.liqear.helpers;

import com.pillowapps.liqear.entities.Track;

import java.util.Comparator;

public class ArtistTrackComparator implements Comparator<Track> {
    @Override
    public int compare(Track track, Track track2) {
        return track.getArtist().compareTo(track2.getArtist());
    }
}
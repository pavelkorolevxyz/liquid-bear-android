package com.pillowapps.liqear.models;

import com.pillowapps.liqear.entities.Track;

import java.util.List;

public class TrackModel {

    public void clearTitles(List<Track> tracks) {
        String regexp = "[^\\w\\s\\?!&#\\-'\\(\\[\\)\\.,:/]+";
        String regexpBrackets = "\\(((?![^)]*(ft.|live|remix|cover|feat)).*?)\\)";
        if (tracks == null) {
            return;
        }
        for (Track track : tracks) {
            String title = track.getTitle().replaceAll(regexp, "").replaceAll(regexpBrackets, "");
            String artist = track.getArtist().replaceAll(regexp, "").replaceAll(regexpBrackets, "");
            track.setTitle(title);
            track.setArtist(artist);
        }
    }
}

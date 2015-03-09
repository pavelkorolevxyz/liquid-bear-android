package com.pillowapps.liqear.helpers;

import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSet;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSetlist;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSets;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmTrack;

import java.util.ArrayList;
import java.util.List;

public class SetlistfmUtils {
    public static List<Track> getTracks(SetlistfmSetlist setlist) {
        List<Track> tracks = new ArrayList<>();
        SetlistfmSets sets = setlist.getSets();
        if (sets == null) return tracks;
        for (SetlistfmSet set : sets.getSets()) {
            if (set == null) continue;
            List<SetlistfmTrack> setlistfmTracks = set.getTracks();
            tracks.addAll(Converter.convertSetlistTracks(setlist.getArtist().getName(),
                    setlistfmTracks));
        }
        return tracks;
    }

    public static ArrayList<String> getStringTracks(SetlistfmSetlist setlist) {
        List<Track> tracks = getTracks(setlist);
        ArrayList<String> titles = new ArrayList<>(tracks.size());
        for (Track track : tracks) {
            titles.add(track.getTitle());
        }
        return titles;
    }
}

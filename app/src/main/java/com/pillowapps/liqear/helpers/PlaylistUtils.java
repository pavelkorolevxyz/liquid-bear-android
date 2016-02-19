package com.pillowapps.liqear.helpers;

import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;

import java.util.List;

public class PlaylistUtils {

    private PlaylistUtils() {
        // no-op
    }

    public static int sizeOf(Playlist playlist) {
        if (playlist == null || playlist.getTracks() == null) {
            return 0;
        }
        return playlist.getTracks().size();
    }

    public static Track getTrack(Playlist playlist, int restoredIndex) {
        List<Track> tracks = playlist.getTracks();
        if (tracks == null) {
            return null;
        }
        if (restoredIndex < tracks.size()) {
            return tracks.get(restoredIndex);
        }
        return null;
    }
}

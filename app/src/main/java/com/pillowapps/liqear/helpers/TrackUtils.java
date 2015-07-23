package com.pillowapps.liqear.helpers;

import com.pillowapps.liqear.entities.Track;

public class TrackUtils {
    public static String getNotation(Track track) {
        return String.format("%s - %s", track.getArtist(), track.getTitle());
    }

    public static boolean vkInfoAvailable(Track track) {
        return track.getAudioId() > 0 && track.getOwnerId() > 0;
    }

    public static String getUrlFromTrack(Track track) {
        if (track.isLocal()) {
            return track.getLocalUrl();
        } else {
            return track.getUrl();
        }
    }
}

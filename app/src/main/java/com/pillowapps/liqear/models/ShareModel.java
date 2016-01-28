package com.pillowapps.liqear.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Track;

public class ShareModel {

    public String createShareMessage(@NonNull Track track, @NonNull Album album, String template) {
        String artist = "";
        String trackTitle = "";
        String albumTitle = "";
        if (track.getArtist() != null) {
            artist = track.getArtist();
        }
        if (track.getTitle() != null) {
            trackTitle = track.getTitle();
        }
        if (album.getTitle() != null) {
            albumTitle = album.getTitle();
        }
        return template.replace("%a%", artist)
                .replace("%t%", trackTitle)
                .replace("%r%", albumTitle);
    }

    @Nullable
    public String getAlbumImageUrl(Track currentTrack, Album currentAlbum) {
        if (currentAlbum == null) {
            return null;
        }
        String currentAlbumArtist = currentAlbum.getArtist();
        String currentTrackArtist = currentTrack.getArtist();
        if (!currentAlbumArtist.equals(currentTrackArtist)) {
            return null;
        }
        return currentAlbum.getImageUrl();
    }
}

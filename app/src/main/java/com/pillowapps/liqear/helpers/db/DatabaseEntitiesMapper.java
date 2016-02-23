package com.pillowapps.liqear.helpers.db;

import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.storio.DBPlaylist;
import com.pillowapps.liqear.entities.storio.DBTrack;

import java.util.ArrayList;
import java.util.List;

public class DatabaseEntitiesMapper {

    private DatabaseEntitiesMapper() {
        // no-op
    }

    public static DBPlaylist map(Playlist playlist) {
        return new DBPlaylist(playlist.getId(), playlist.getTitle(), playlist.isMainPlaylist());
    }

    public static Playlist map(DBPlaylist dbPlaylist) {
        final Playlist playlist = new Playlist();
        if (dbPlaylist == null) {
            return playlist;
        }
        playlist.setId(dbPlaylist.getId());
        playlist.setTitle(dbPlaylist.getTitle());
        playlist.setMainPlaylist(dbPlaylist.isMainPlaylist());
        return playlist;
    }

    public static DBTrack map(Track track) {
        DBTrack dbTrack = new DBTrack(track.getArtist(), track.getTitle());
        dbTrack.setAudioId(track.getAudioId());
        dbTrack.setOwnerId(track.getOwnerId());
        dbTrack.setAlbum(track.getAlbum());
        dbTrack.setLocal(track.isLocal());
        dbTrack.setLocalUrl(track.getLocalUrl());
        return dbTrack;
    }

    public static Track map(DBTrack dbTrack) {
        Track track = new Track(dbTrack.getArtist(), dbTrack.getTitle());
        track.setAudioId(dbTrack.getAudioId());
        track.setOwnerId(dbTrack.getOwnerId());
        track.setAlbum(dbTrack.getAlbum());
        track.setLocal(dbTrack.isLocal());
        track.setLocalUrl(dbTrack.getLocalUrl());
        return track;
    }

    public static List<Track> mapListOfDBTracks(List<DBTrack> dbTracks) {
        List<Track> tracks = new ArrayList<>(dbTracks.size());
        for (DBTrack dbTrack : dbTracks) {
            tracks.add(map(dbTrack));
        }
        return tracks;
    }

    public static List<Playlist> mapListOfDBPlaylists(List<DBPlaylist> dbPlaylists) {
        List<Playlist> playlists = new ArrayList<>(dbPlaylists.size());
        for (DBPlaylist dbPlaylist : dbPlaylists) {
            playlists.add(map(dbPlaylist));
        }
        return playlists;
    }

    public static List<DBTrack> mapListOfTracks(List<Track> tracks) {
        List<DBTrack> dbTracks = new ArrayList<>(tracks.size());
        for (Track track : tracks) {
            dbTracks.add(map(track));
        }
        return dbTracks;
    }
}

package com.pillowapps.liqear.callbacks;

import com.pillowapps.liqear.entities.Playlist;

import java.util.List;

public interface GetPlaylistListCallback {
    public void onCompleted(List<Playlist> playlists);
}

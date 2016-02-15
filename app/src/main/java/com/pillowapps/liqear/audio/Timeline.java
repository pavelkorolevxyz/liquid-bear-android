package com.pillowapps.liqear.audio;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.PlayingState;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.RepeatMode;
import com.pillowapps.liqear.entities.ShuffleMode;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.PlaylistUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

public class Timeline {
    private Playlist currentPlaylist;
    private int index = 0;

    private String currentArtistImageUrl;

    private PlayingState playingState = PlayingState.DEFAULT;
    private ListeningsCounter listeningsCounter;
    private LinkedList<Integer> queueIndexes;
    private Stack<Integer> previousTracksIndexes;
    private boolean autoplay = false;

    private ShuffleMode shuffleMode = ShuffleMode.DEFAULT;
    private RepeatMode repeatMode = RepeatMode.REPEAT_PLAYLIST;

    private Album currentAlbum;
    private Album previousAlbum;
    private Bitmap albumCoverBitmap;

    private boolean playlistChanged = false;

    private PlayingState playingStateBeforeCall = PlayingState.DEFAULT;
    private String previousArtist;
    private int position;

    @Inject
    public Timeline(ListeningsCounter listeningsCounter) {
        this.listeningsCounter = listeningsCounter;

        this.previousTracksIndexes = new Stack<>();
        this.queueIndexes = new LinkedList<>();

//        this.shuffleMode = SharedPreferencesManager.getPreferences()
//                .getBoolean(Constants.SHUFFLE_MODE_ON, false)
//                ? ShuffleMode.SHUFFLE
//                : ShuffleMode.DEFAULT;
//        this.repeatMode = SharedPreferencesManager.getPreferences()
//                .getBoolean(Constants.REPEAT_MODE_ON, false)
//                ? RepeatMode.REPEAT
//                : RepeatMode.REPEAT_PLAYLIST; todo
    }

    public ShuffleMode getShuffleMode() {
        return shuffleMode;
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public void setPlaylist(@NonNull Playlist playlist) {
        this.currentPlaylist = playlist;
        this.listeningsCounter.updateWithPlaylist(playlist);
    }

    public void toggleShuffle() {
        switch (shuffleMode) {
            case SHUFFLE:
                shuffleMode = ShuffleMode.DEFAULT;
                break;
            case DEFAULT:
                shuffleMode = ShuffleMode.SHUFFLE;
                break;
        }
    }

    public void toggleRepeat() {
        switch (repeatMode) {
            case REPEAT_PLAYLIST:
                repeatMode = RepeatMode.REPEAT;
                break;
            case REPEAT:
                repeatMode = RepeatMode.REPEAT_PLAYLIST;
                break;
        }
    }

    public Album getCurrentAlbum() {
        return currentAlbum;
    }

    public void setCurrentAlbum(Album album) {
        if (album != null) {
            previousAlbum = getCurrentAlbum();
        }
        currentAlbum = album;
    }

    @Nullable
    public Track getCurrentTrack() {
        if (currentPlaylist == null || PlaylistUtils.sizeOf(currentPlaylist) <= index) {
            return null;
        }
        return currentPlaylist.getTracks().get(index);
    }

    public void queueTrack(int index) {
        queueIndexes.add(index);
    }

    public int getNextIndex() {
        if (shuffleMode == ShuffleMode.SHUFFLE) {
            return getRandomIndex();
        } else {
            return (index + 1) % PlaylistUtils.sizeOf(currentPlaylist);
        }
    }

    public Track getNextTrack() {
        if (currentPlaylist == null || PlaylistUtils.sizeOf(currentPlaylist) <= index) return null;
        return currentPlaylist.getTracks().get(getRandomIndex());
    }

    public int getRandomIndex() {
        return listeningsCounter.getLeastPlayedRandomIndex();
    }

    public int getPrevTrackIndex() {
        if (previousTracksIndexes.isEmpty()) {
            return index;
        }
        return previousTracksIndexes.pop();
    }

    public List<Track> getPlaylistTracks() {
        if (currentPlaylist == null) return null;
        return currentPlaylist.getTracks();
    }

    public int getIndex() {
        return index;
    }

    public void listen(int index) {
        listeningsCounter.listen(index);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCurrentArtistImageUrl() {
        return currentArtistImageUrl;
    }

    public void setCurrentArtistImageUrl(String currentArtistImageUrl) {
        this.currentArtistImageUrl = currentArtistImageUrl;
    }

    public PlayingState getPlayingState() {
        return playingState;
    }

    public void setPlayingState(PlayingState playingState) {
        this.playingState = playingState;
    }

    public Stack<Integer> getPreviousTracksIndexes() {
        return previousTracksIndexes;
    }

    public Album getPreviousAlbum() {
        return previousAlbum;
    }

    public LinkedList<Integer> getQueueIndexes() {
        return queueIndexes;
    }

    public boolean isPlaylistChanged() {
        return playlistChanged;
    }

    public void clearQueue() {
        queueIndexes.clear();
    }

    public void clearPreviousIndexes() {
        previousTracksIndexes.clear();
    }

    public void addToPlaylist(List<Track> tracks) {
        if (tracks == null || currentPlaylist == null) return;
        currentPlaylist.getTracks().addAll(tracks);
    }

    public void addToPlaylist(Track track) {
        currentPlaylist.getTracks().add(track);
    }

    public Bitmap getAlbumCoverBitmap() {
        return albumCoverBitmap;
    }

    public void setAlbumCoverBitmap(Bitmap albumCoverBitmap) {
        this.albumCoverBitmap = albumCoverBitmap;
    }

    public void setPlaylistChanged(boolean playlistChanged) {
        this.playlistChanged = playlistChanged;
    }

    public void addToPrevIndexes(int index) {
        previousTracksIndexes.add(index);
    }

    public PlayingState getPlayingStateBeforeCall() {
        return playingStateBeforeCall;
    }

    public void setPlayingStateBeforeCall(PlayingState playingStateBeforeCall) {
        this.playingStateBeforeCall = playingStateBeforeCall;
    }

    public String getPreviousArtist() {
        return previousArtist;
    }

    public void setPreviousArtist(String previousArtist) {
        this.previousArtist = previousArtist;
    }

    public Playlist getPlaylist() {
        return currentPlaylist;
    }

    public boolean isAutoplay() {
        return autoplay;
    }

    public void setAutoplay(boolean autoplay) {
        this.autoplay = autoplay;
    }

    public void updateRealTrackPositions() {
        List<Track> playlist = getPlaylistTracks();
        for (int i = 0; i < playlist.size(); i++) {
            playlist.get(i).setRealPosition(i);
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setCurrentTrackDuration(int duration) {
        Track currentTrack = getCurrentTrack();
        if (currentTrack == null) {
            return;
        }
        currentTrack.setDuration(duration);
    }
}

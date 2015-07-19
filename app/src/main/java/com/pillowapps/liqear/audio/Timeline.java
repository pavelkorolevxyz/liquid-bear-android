package com.pillowapps.liqear.audio;

import android.graphics.Bitmap;

import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.RepeatMode;
import com.pillowapps.liqear.entities.ShuffleMode;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PlaylistUtils;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.models.PlayingState;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;


public class Timeline {
    private static Timeline instance;
    private Playlist currentPlaylist;
    private int index = 0;

    private String currentArtistImageUrl;

    private PlayingState playingState = PlayingState.DEFAULT;
    private int timePosition = 0;
    private int[] listeningsCount;
    private int maxListeningsCount;
    private LinkedList<Integer> queueIndexes = new LinkedList<>();
    private Stack<Integer> previousTracksIndexes = new Stack<>();
    private boolean startPlayingOnPrepared = false;

    private ShuffleMode shuffleMode = SharedPreferencesManager.getPreferences()
            .getBoolean(Constants.SHUFFLE_MODE_ON, false)
            ? ShuffleMode.SHUFFLE
            : ShuffleMode.DEFAULT;
    private RepeatMode repeatMode = SharedPreferencesManager.getPreferences()
            .getBoolean(Constants.REPEAT_MODE_ON, false)
            ? RepeatMode.REPEAT
            : RepeatMode.REPEAT_PLAYLIST;

    private Album currentAlbum;
    private Album previousAlbum;
    private Bitmap albumCoverBitmap;

    private boolean playlistChanged = false;

    private PlayingState playingStateBeforeCall = PlayingState.DEFAULT;
    private String previousArtist;

    private Timeline() {
        // No operations.
    }

    public static Timeline getInstance() {
        if (instance == null) instance = new Timeline();
        return instance;
    }

    public ShuffleMode getShuffleMode() {
        return shuffleMode;
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public void setPlaylist(Playlist playlist) {
        this.currentPlaylist = playlist;
        if (playlist == null) return;
        listeningsCount = new int[playlist.getTracks().size()];
        maxListeningsCount = 1;
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

    public Track getCurrentTrack() {
        if (currentPlaylist == null || PlaylistUtils.sizeOf(currentPlaylist) <= index) return null;
        return currentPlaylist.getTracks().get(index);
    }

    public void queueTrack(int index) {
        queueIndexes.add(index);
    }

    public Track getNextTrack() {
        if (currentPlaylist == null || PlaylistUtils.sizeOf(currentPlaylist) <= index) return null;
        return currentPlaylist.getTracks().get(getRandomIndex());
    }

    public int getRandomIndex() {
        int playlistSize = currentPlaylist.getTracks().size();
        int randomIndex = (new Random().nextInt(playlistSize));
        while (true) {
            if (listeningsCount[randomIndex] < maxListeningsCount) {
                break;
            } else {
                randomIndex = (randomIndex + 1) % playlistSize;
            }
            boolean allTracksListeningsAreEqual = true;
            for (int i = 0; i < playlistSize; i++) {
                if (listeningsCount[i] < maxListeningsCount) {
                    allTracksListeningsAreEqual = false;
                    break;
                }
            }
            if (allTracksListeningsAreEqual) {
                maxListeningsCount++;
            }
        }
        return randomIndex;
    }

    public List<Track> getPlaylistTracks() {
        if (currentPlaylist == null) return null;
        return currentPlaylist.getTracks();
    }

    public int getIndex() {
        return index;
    }

    public void setTimePosition(int timePosition) {
        this.timePosition = timePosition;
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

    public boolean isStartPlayingOnPrepared() {
        return startPlayingOnPrepared;
    }

    public void setStartPlayingOnPrepared(boolean startPlayingOnPrepared) {
        this.startPlayingOnPrepared = startPlayingOnPrepared;
    }
}

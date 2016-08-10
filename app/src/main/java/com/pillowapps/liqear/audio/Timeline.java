package com.pillowapps.liqear.audio;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.RepeatMode;
import com.pillowapps.liqear.entities.ShuffleMode;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.PlaylistUtils;
import com.pillowapps.liqear.helpers.SavesManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

public class Timeline {
    private ListeningsCounter listeningsCounter;

    private Playlist currentPlaylist;

    private int index;

    private String currentArtistImageUrl;
    private LinkedList<Integer> queueIndexes;
    private Stack<Integer> previousTracksIndexes;

    private ShuffleMode shuffleMode;
    private RepeatMode repeatMode;

    private Album currentAlbum;
    private Album previousAlbum;
    private Bitmap albumCoverBitmap;

    private String previousArtist;
    private int position;

    private boolean playing;

    @Inject
    public Timeline(ListeningsCounter listeningsCounter, SavesManager savesManager) {
        this.listeningsCounter = listeningsCounter;

        this.previousTracksIndexes = new Stack<>();
        this.queueIndexes = new LinkedList<>();

        this.shuffleMode = savesManager.getShuffleMode();
        this.repeatMode = savesManager.getRepeatMode();

        this.playing = false;

        this.currentPlaylist = new Playlist();
        this.index = 0;
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
        clearPreviousIndexes();
        clearQueue();
        updateRealTrackPositions();
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
        switch (shuffleMode) {
            case SHUFFLE:
                return getRandomIndex();
            default:
                return (index + 1) % PlaylistUtils.sizeOf(currentPlaylist);
        }
    }

    public int getRandomIndex() {
        return listeningsCounter.getLeastPlayedRandomIndex();
    }

    public int getPrevTrackIndex() {
        switch (shuffleMode) {
            case SHUFFLE:
                if (previousTracksIndexes.size() < 2) {
                    return index;
                }
                previousTracksIndexes.pop();
                return previousTracksIndexes.pop();
            default:
                return index - 1 > 0 ? index - 1 : PlaylistUtils.sizeOf(currentPlaylist) - 1;
        }

    }

    public List<Track> getPlaylistTracks() {
        if (currentPlaylist == null) {
            return null;
        }
        return currentPlaylist.getTracks();
    }

    public int getIndex() {
        return index;
    }

    public void listen(int index) {
        listeningsCounter.listen(index);
        addToPrevIndexes(index);
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

    public Album getPreviousAlbum() {
        return previousAlbum;
    }

    public LinkedList<Integer> getQueueIndexes() {
        return queueIndexes;
    }

    public void clearQueue() {
        queueIndexes.clear();
    }

    public void clearPreviousIndexes() {
        previousTracksIndexes.clear();
    }

    public void addToPlaylist(List<Track> tracks) {
        if (tracks == null || currentPlaylist == null) {
            return;
        }
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

    public void addToPrevIndexes(int index) {
        previousTracksIndexes.add(index);
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

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void removeTrack(int index) {
        currentPlaylist.getTracks().remove(index);
    }

    public Track getTrack(int index) {
        List<Track> tracks = getPlaylistTracks();
        if (tracks.size() > index) {
            return tracks.get(index);
        } else {
            return null;
        }
    }

    public void updateTracks(List<Track> tracks) {
        currentPlaylist.setTracks(tracks);
        updateRealTrackPositions();
    }
}

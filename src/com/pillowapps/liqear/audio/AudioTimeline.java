package com.pillowapps.liqear.audio;

import android.graphics.Bitmap;
import android.widget.Toast;
import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.models.Album;
import com.pillowapps.liqear.models.Track;

import java.util.*;

public class AudioTimeline {
    private static List<Track> playlist;
    private static int currentIndex = 0;
    private static int currentPosition;
    private static Album album;
    private static MusicPlaybackService musicPlaybackService;
    private static LinkedList<Integer> queue = new LinkedList<Integer>();
    private static boolean stateActive = false;
    private static int[] listenings;
    private static int currentListeningsMax;
    private static boolean stillLastPlaylist = false;
    private static boolean playingBeforeCall = false;
    private static int withoutUrls = 0;
    private static String imageUrl;
    private static boolean playlistChanged = false;
    private static boolean playing;
    private static String previousArtist;
    private static Album previousAlbum;
    private static ShuffleMode shuffleMode = PreferencesManager.getPreferences()
            .getBoolean(Constants.SHUFFLE_MODE_ON, false) ? ShuffleMode.SHUFFLE :
            ShuffleMode.DEFAULT;
    private static RepeatMode repeatMode = PreferencesManager.getPreferences()
            .getBoolean(Constants.REPEAT_MODE_ON, false)
            ? RepeatMode.REPEAT : RepeatMode.REPEAT_PLAYLIST;
    private static Stack<Integer> prevTracksIndexes = new Stack<Integer>();
    private static List<Integer> prevClickedItems = new ArrayList<Integer>();
    private static Bitmap currentAlbumBitmap;

    public static void toggleShuffle() {
        switch (getShuffleMode()) {
            case SHUFFLE:
                setShuffleMode(ShuffleMode.DEFAULT);
                break;
            case DEFAULT:
                setShuffleMode(ShuffleMode.SHUFFLE);
                break;
            default:
                break;
        }
    }

    public static void toggleRepeat() {
        switch (getRepeatMode()) {
            case REPEAT_PLAYLIST:
                setRepeatMode(RepeatMode.REPEAT);
                break;
            case REPEAT:
                setRepeatMode(RepeatMode.REPEAT_PLAYLIST);
                break;
            default:
                break;
        }
    }

    public static void clearPreviousList() {
        prevTracksIndexes.clear();
    }

    public static ShuffleMode getShuffleMode() {
        return shuffleMode;
    }

    public static void setShuffleMode(ShuffleMode mode) {
        shuffleMode = mode;
    }

    public static RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public static void setRepeatMode(RepeatMode mode) {
        repeatMode = mode;
    }

    public static Album getPreviousAlbum() {
        return previousAlbum;
    }

    public static void setPreviousAlbum(Album previousAlbum) {
        AudioTimeline.previousAlbum = previousAlbum;
    }

    public static boolean hasSaves() {
        return playlist != null && playlist.size() > 0;
    }

    public static boolean hasCurrentTrack() {
        return getCurrentTrack() != null;
    }

    public static List<Track> getPlaylist() {
        return playlist;
    }

    public static void setPlaylist(List<Track> playlist) {
        AudioTimeline.playlist = playlist;
        listenings = new int[playlist.size()];
        currentListeningsMax = 1;
        withoutUrls = 0;
    }

    public static int getPlaylistSize() {
        if (playlist == null) return 0;
        return playlist.size();
    }

    public static void addToPlaylist(List<Track> tracks) {
        if (AudioTimeline.playlist == null) {
            AudioTimeline.playlist = new ArrayList<Track>(tracks);
        } else {
            AudioTimeline.playlist.addAll(tracks);
        }
        listenings = new int[playlist.size()];
        playlistChanged = true;
    }

    public static void addToPlaylist(Track track) {
        if (AudioTimeline.playlist == null) {
            AudioTimeline.playlist = new ArrayList<Track>(1);
        } else {
            AudioTimeline.playlist.add(track);
        }
        listenings = new int[playlist.size()];
        playlistChanged = true;
        Toast.makeText(LiqearApplication.getAppContext(), R.string.added,
                Toast.LENGTH_SHORT).show();
    }

    public static int getCurrentIndex() {
        return currentIndex;
    }

    public static void setCurrentIndex(int currentIndex) {
        AudioTimeline.currentIndex = currentIndex;
        listenings[currentIndex]++;
    }

    public static int getCurrentPosition() {
        return currentPosition;
    }

    public static void setCurrentPosition(int position) {
        AudioTimeline.currentPosition = position;
    }

    public static Track getCurrentTrack() {
        if (playlist == null || playlist.size() == 0) {
            return null;
        }
        if (currentIndex > playlist.size() - 1) {
            currentIndex = 0;
        }
        return playlist.get(currentIndex);
    }

    public static MusicPlaybackService getMusicPlaybackService() {
        return musicPlaybackService;
    }

    public static void setMusicPlaybackService(
            MusicPlaybackService musicPlaybackService) {
        AudioTimeline.musicPlaybackService = musicPlaybackService;
    }

    public static LinkedList<Integer> getQueue() {
        return AudioTimeline.queue;
    }

    public static void setQueue(LinkedList<Integer> linkedList) {
        AudioTimeline.queue = linkedList;
    }

    public static boolean isStateActive() {
        return stateActive;
    }

    public static void setStateActive(boolean stateActive) {
        AudioTimeline.stateActive = stateActive;
    }

    public static int calculateRandomIndex() {
        int index = (new Random().nextInt(playlist.size()));
        while (true) {
            if (listenings[index] < currentListeningsMax) {
                break;
            } else {
                index = (index + 1) % playlist.size();
            }
            boolean allMax = true;
            for (int i = 0; i < playlist.size(); i++) {
                if (listenings[i] < currentListeningsMax) {
                    allMax = false;
                    break;
                }
            }
            if (allMax) {
                currentListeningsMax++;
            }
        }
        return index;
    }

    public static boolean isStillLastPlaylist() {
        return stillLastPlaylist;
    }

    public static void setStillLastPlaylist(boolean stillLastPlaylist) {
        AudioTimeline.stillLastPlaylist = stillLastPlaylist;
    }

    public static void savePlayingState() {
        playingBeforeCall = AudioTimeline.isStateActive();
    }

    public static boolean wasPlayingBeforeCall() {
        return playingBeforeCall;
    }

    public static void incrementWithoutUrl() {
        withoutUrls++;
    }

    public static int getWithoutUrls() {
        return withoutUrls;
    }

    public static void clearOnlinePlaylist() {
        if (playlist == null) return;
        for (int i = playlist.size() - 1; i >= 0; i--) {
            Track track = playlist.get(i);
            if (!track.isLocal()) {
                playlist.remove(i);
            }
        }
    }

    public static Track getTrack(int position) {
        return playlist.get(position);
    }

    public static Album getAlbum() {
        return album;
    }

    public static void setCurrentAlbum(Album album) {
        if (album != null) {
            previousAlbum = AudioTimeline.getAlbum();
        }
        AudioTimeline.album = album;
    }

    public static String getImageUrl() {
        return imageUrl;
    }

    public static void setImageUrl(String imageUrl) {
        AudioTimeline.imageUrl = imageUrl;
    }

    public static boolean isPlaylistChanged() {
        return playlistChanged;
    }

    public static void setPlaylistChanged(boolean playlistChanged) {
        AudioTimeline.playlistChanged = playlistChanged;
    }

    public static boolean isPlaying() {
        return playing;
    }

    public static void setPlaying(boolean playing) {
        AudioTimeline.playing = playing;
    }

    public static String getPreviousArtist() {
        return previousArtist;
    }

    public static void setPreviousArtist(String previousArtist) {
        AudioTimeline.previousArtist = previousArtist;
    }

    public static void addToPrevClicked(int prevPosition) {
        AudioTimeline.prevClickedItems.add(prevPosition);
    }

    public static void addToPrevIndexes(int currentIndex) {
        prevTracksIndexes.push(currentIndex);
    }

    public static int peekPrevIndexes() {
        return prevTracksIndexes.peek();
    }

    public static Stack<Integer> getPrevTracksIndexes() {
        return prevTracksIndexes;
    }

    public static void setPrevTracksIndexes(Stack<Integer> prevTracksIndexes) {
        AudioTimeline.prevTracksIndexes = prevTracksIndexes;
    }

    public static List<Integer> getPrevClickedItems() {
        return prevClickedItems;
    }

    public static void clearPrevClickedItems() {
        Integer integer = null;
        try {
            integer = prevClickedItems.get(prevClickedItems.size() - 1);
        } catch (Exception ignored) {
        }
        prevClickedItems.clear();
        if (integer != null) {
            prevClickedItems.add(integer);
        }
    }

    public static Bitmap getCurrentAlbumBitmap() {
        return currentAlbumBitmap;
    }

    public static void setCurrentAlbumBitmap(Bitmap currentAlbumBitmap) {
        AudioTimeline.currentAlbumBitmap = currentAlbumBitmap;
    }
}

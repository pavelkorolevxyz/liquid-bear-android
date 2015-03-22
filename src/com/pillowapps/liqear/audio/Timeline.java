package com.pillowapps.liqear.audio;

import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.RepeatMode;
import com.pillowapps.liqear.entities.ShuffleMode;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PreferencesManager;

import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

public class Timeline {
    private Timeline instance;

    private Playlist currentPlaylist;
    private int index = 0;
    private int timePosition = 0;

    private int[] listeningsCount;
    private int maxListeningsCount;

    private static LinkedList<Integer> queueIndexes = new LinkedList<>();
    private static Stack<Integer> previousTracksIndexes = new Stack<>();
    private ShuffleMode shuffleMode = PreferencesManager.getPreferences()
            .getBoolean(Constants.SHUFFLE_MODE_ON, false)
            ? ShuffleMode.SHUFFLE
            : ShuffleMode.DEFAULT;
    private RepeatMode repeatMode = PreferencesManager.getPreferences()
            .getBoolean(Constants.REPEAT_MODE_ON, false)
            ? RepeatMode.REPEAT
            : RepeatMode.REPEAT_PLAYLIST;


    private Timeline() {
    }

    public Timeline getInstance() {
        if (instance == null) instance = new Timeline();
        return instance;
    }

    public void setPlaylist(Playlist playlist) {
        this.currentPlaylist = playlist;
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

    public Track getCurrentTrack() {
        if (currentPlaylist == null || currentPlaylist.size() <= index) return null;
        return currentPlaylist.getTracks().get(index);
    }

    public void queueTrack(int index) {
        queueIndexes.add(index);
    }

    public void getNextTrack() {
        //todo
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
}

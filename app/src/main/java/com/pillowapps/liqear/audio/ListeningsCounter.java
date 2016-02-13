package com.pillowapps.liqear.audio;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.entities.Playlist;

import java.util.Random;

import javax.inject.Inject;

public class ListeningsCounter {

    private int[] listeningsCount;
    private int maxListeningsCount;
    private int minListeningsCount;

    private Random random;

    @Inject
    public ListeningsCounter(Random random) {
        this.random = random;
    }

    public void updateWithPlaylist(@NonNull Playlist playlist) {
        listeningsCount = new int[playlist.getTracks().size()];
        maxListeningsCount = 1;
        minListeningsCount = 0;
    }

    public int getLeastPlayedRandomIndex() {
        int count = listeningsCount.length;
        int randomIndex = random.nextInt(count);
        int tries = 0;
        while (tries++ < count) {
            if (listeningsCount[randomIndex] == minListeningsCount) {
                break;
            }
            randomIndex = (randomIndex + 1) % count;
        }
        if (tries >= count) {
            minListeningsCount++;
        }
        return randomIndex;
    }

    public void listen(int index) {
        listeningsCount[index]++;
        if (listeningsCount[index] > maxListeningsCount) {
            maxListeningsCount = listeningsCount[index];
        }
    }
}

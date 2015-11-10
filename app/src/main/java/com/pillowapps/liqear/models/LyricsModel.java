package com.pillowapps.liqear.models;

import android.content.Context;
import android.content.Intent;

import com.pillowapps.liqear.activities.TextActivity;
import com.pillowapps.liqear.entities.Track;

public class LyricsModel {
    public void openLyrics(Context context, Track track) {
        Intent intent = new Intent(context, TextActivity.class);
        intent.putExtra("artist", track.getArtist());
        intent.putExtra("title", track.getTitle());
        intent.putExtra(TextActivity.TEXT_AIM, TextActivity.Aim.LYRICS); // todo ??
        context.startActivity(intent);
    }
}

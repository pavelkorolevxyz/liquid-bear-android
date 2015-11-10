package com.pillowapps.liqear.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.pillowapps.liqear.entities.Track;

public class VideoModel {

    public void openVideo(Context context, Track track) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.setPackage("com.google.android.youtube");
            intent.putExtra("query", String.format("%s %s official video",
                    track.getArtist(), track.getTitle()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            String url = String.format("http://m.youtube.com/results?&q=%s",
                    Uri.encode(track.getArtist() + " " + track.getTitle() + " official video"));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        }
    }

}

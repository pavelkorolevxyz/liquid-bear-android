package com.pillowapps.liqear.helpers;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.os.Build;
import android.text.Html;

import com.pillowapps.liqear.audio.MediaButtonReceiver;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.entities.PlayingState;
import com.pillowapps.liqear.entities.Track;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CompatIcs {
    private static RemoteControlClient sRemote;

    private CompatIcs() {
        // no-op
    }

    public static void registerRemote(Context context, AudioManager am) {
        MediaButtonReceiver.registerMediaButton(context);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(new ComponentName(context.getPackageName(),
                MediaButtonReceiver.class.getName()));
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(context, 0,
                mediaButtonIntent, 0);
        RemoteControlClient remote = new RemoteControlClient(mediaPendingIntent);
        int flags = RemoteControlClient.FLAG_KEY_MEDIA_NEXT
                | RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
                | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE
                | RemoteControlClient.FLAG_KEY_MEDIA_PLAY
                | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE;
        remote.setTransportControlFlags(flags);
        am.registerRemoteControlClient(remote);
        sRemote = remote;
    }

    public static void updateRemote(final Context context, final Track track) {
        final RemoteControlClient remote = sRemote;
        if (remote == null || track == null) {
            return;
        }
//        remote.setPlaybackState(Timeline.getInstance().getPlayingState() == PlayingState.PLAYING ?
//                        RemoteControlClient.PLAYSTATE_PLAYING :
//                        RemoteControlClient.PLAYSTATE_PAUSED
//        );
//        RemoteControlClient.MetadataEditor editor = remote.editMetadata(true);
//        editor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST,
//                Html.fromHtml(track.getArtist()).toString());
//        editor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST,
//                Html.fromHtml(track.getArtist()).toString());
//        editor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE,
//                Html.fromHtml(track.getTitle()).toString());
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = Timeline.getInstance().getAlbumCoverBitmap();
//        editor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, bitmap);
//        editor.apply(); //todo
    }

    public static void unregisterRemote(Context context, AudioManager am) {
        am.unregisterRemoteControlClient(sRemote);
        MediaButtonReceiver.unregisterMediaButton(context);
    }
}

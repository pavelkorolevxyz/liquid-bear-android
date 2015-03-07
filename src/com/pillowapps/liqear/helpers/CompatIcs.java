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

import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;
import com.pillowapps.liqear.audio.AudioTimeline;
import com.pillowapps.liqear.audio.MediaButtonReceiver;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Track;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CompatIcs {
    private static RemoteControlClient sRemote;

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

    public static void updateRemote(Context context, final Track track) {
        final RemoteControlClient remote = sRemote;
        if (remote == null || track == null) {
            return;
        }
        remote.setPlaybackState(AudioTimeline.isPlaying() ?
                        RemoteControlClient.PLAYSTATE_PLAYING :
                        RemoteControlClient.PLAYSTATE_PAUSED
        );
        new Thread(new Runnable() {
            @Override
            public void run() {
                RemoteControlClient.MetadataEditor editor = remote.editMetadata(true);
                editor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST,
                        Html.fromHtml(track.getArtist()).toString());
                editor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST,
                        Html.fromHtml(track.getArtist()).toString());
                editor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE,
                        Html.fromHtml(track.getTitle()).toString());

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Album album = AudioTimeline.getAlbum();
                Bitmap bitmap = null;
                if (album != null) {
                    InputStream sourceStream = null;
                    String imageUrl = album.getImageUrl();
                    if (imageUrl == null) {
                        return;
                    }
                    DiscCacheAware discCache = ImageLoader.getInstance().getDiscCache();
                    if (discCache == null) {
                        return;
                    }
                    File cachedImage = discCache.get(imageUrl);
                    if (cachedImage == null) {
                        return;
                    }
                    try {
                        if (cachedImage.exists()) { // if image was cached by UIL
                            sourceStream = new FileInputStream(cachedImage);
                            bitmap = BitmapFactory.decodeStream(sourceStream);
                        } else if (PreferencesManager.getPreferences().getBoolean(
                                Constants.DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true)) {
                            ImageDownloader downloader = new URLConnectionImageDownloader();
                            sourceStream = downloader.getStream(new URI(imageUrl));
                            bitmap = BitmapFactory.decodeStream(sourceStream);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } finally {
                        if (sourceStream != null) {
                            try {
                                sourceStream.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
                }
                try {
                    editor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, bitmap);
                } catch (Exception ignored) {
                }
                editor.apply();
            }
        }).start();
    }

    public static void unregisterRemote(Context context, AudioManager am) {
        am.unregisterRemoteControlClient(sRemote);
        MediaButtonReceiver.unregisterMediaButton(context);
    }
}

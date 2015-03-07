package com.pillowapps.liqear.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.Html;
import android.widget.RemoteViews;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.audio.AudioTimeline;
import com.pillowapps.liqear.audio.MusicPlaybackService;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.helpers.Utils;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Track;

public class FourWidthThreeHeightWidget extends AppWidgetProvider {
    private static boolean sEnabled;

    public static void checkEnabled(Context context, AppWidgetManager manager) {
        sEnabled = manager.getAppWidgetIds(new ComponentName(context,
                FourWidthThreeHeightWidget.class)).length != 0;
    }

    public static void updateWidget(final Context context, final AppWidgetManager manager,
                                    boolean playing) {
        if (!sEnabled)
            return;

        final RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout_4x3);

        Track track = AudioTimeline.getCurrentTrack();
        String artist;
        String title;
        SharedPreferences savePreferences = PreferencesManager.getSavePreferences();
        if (track != null) {
            artist = track.getArtist();
            title = track.getTitle();
            SharedPreferences.Editor edit = savePreferences.edit();
            edit.putString(Constants.WIDGET_ARTIST, artist)
                    .putString(Constants.WIDGET_TITLE, title).commit();
        } else {
            artist = savePreferences.getString(Constants.WIDGET_ARTIST, "");
            title = savePreferences.getString(Constants.WIDGET_TITLE, "");
        }
        views.setTextViewText(R.id.artist, Html.fromHtml(artist));
        views.setTextViewText(R.id.title, Html.fromHtml(title));

        int playButton = playing ? R.drawable.pause_button_states : R.drawable.play_button_states;
        views.setInt(R.id.play_pause, "setImageResource", playButton);

        int shuffleButton = Utils.getShuffleButtonImage();
        views.setInt(R.id.shuffle_button, "setImageResource", shuffleButton);

        int repeatButton = Utils.getRepeatButtonImage();
        views.setInt(R.id.repeat_button, "setImageResource", repeatButton);

        int loveButton = Utils.getLoveButtonImage();
        views.setInt(R.id.love_button, "setImageResource", loveButton);


        ComponentName service = new ComponentName(context, MusicPlaybackService.class);

        Intent playPause = new Intent(MusicPlaybackService.ACTION_TOGGLE_PLAYBACK_NOTIFICATION);
        playPause.setComponent(service);
        views.setOnClickPendingIntent(R.id.play_pause,
                PendingIntent.getService(context, 0, playPause, 0));

        Intent nextIntent = new Intent(MusicPlaybackService.ACTION_NEXT);
        nextIntent.setComponent(service);
        views.setOnClickPendingIntent(R.id.next,
                PendingIntent.getService(context, 0, nextIntent, 0));

        Intent prevIntent = new Intent(MusicPlaybackService.ACTION_PREV);
        prevIntent.setComponent(service);
        views.setOnClickPendingIntent(R.id.prev,
                PendingIntent.getService(context, 0, prevIntent, 0));

        Intent closeIntent = new Intent(MusicPlaybackService.ACTION_CLOSE);
        closeIntent.setComponent(service);
        views.setOnClickPendingIntent(R.id.force_close,
                PendingIntent.getService(context, 0, closeIntent, 0));

        Intent shuffleIntent = new Intent(MusicPlaybackService.ACTION_SHUFFLE);
        shuffleIntent.setComponent(service);
        views.setOnClickPendingIntent(R.id.shuffle_button,
                PendingIntent.getService(context, 0, shuffleIntent, 0));

        Intent repeatIntent = new Intent(MusicPlaybackService.ACTION_REPEAT);
        repeatIntent.setComponent(service);
        views.setOnClickPendingIntent(R.id.repeat_button,
                PendingIntent.getService(context, 0, repeatIntent, 0));

        Intent loveIntent = new Intent(MusicPlaybackService.ACTION_LOVE);
        loveIntent.setComponent(service);
        views.setOnClickPendingIntent(R.id.love_button,
                PendingIntent.getService(context, 0, loveIntent, 0));

        Intent addToVkIntent = new Intent(MusicPlaybackService.ACTION_ADD_TO_VK);
        addToVkIntent.setComponent(service);
        views.setOnClickPendingIntent(R.id.add_to_vk_button,
                PendingIntent.getService(context, 0, addToVkIntent, 0));

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        views.setOnClickPendingIntent(R.id.imageView, activity);
        views.setOnClickPendingIntent(R.id.clicable_widget_part2, activity);
        views.setOnClickPendingIntent(R.id.clicable_widget_part3, activity);

        Album albumFromTimeline = AudioTimeline.getAlbum();
        if (albumFromTimeline == null) {
            albumFromTimeline = new Album();
            String imageUrl = savePreferences.getString(Constants.WIDGET_ALBUM_IMAGE, null);
            albumFromTimeline.setImageUrl(imageUrl);
        } else {
            savePreferences.edit().putString(Constants.WIDGET_ALBUM_IMAGE,
                    albumFromTimeline.getImageUrl()).commit();
        }
        Bitmap bitmap = AudioTimeline.getCurrentAlbumBitmap();
        if (bitmap == null) {
            views.setInt(R.id.album_cover_image_view, "setImageResource", R.drawable.lb_icon_white);
        } else {
            views.setImageViewBitmap(R.id.album_cover_image_view, bitmap);
        }
        manager.updateAppWidget(new ComponentName(context,
                FourWidthThreeHeightWidget.class), views);
    }

    @Override
    public void onEnabled(Context context) {
        sEnabled = true;
    }

    @Override
    public void onDisabled(Context context) {
        sEnabled = false;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        sEnabled = true;
        updateWidget(context, manager, AudioTimeline.isPlaying());
    }
}

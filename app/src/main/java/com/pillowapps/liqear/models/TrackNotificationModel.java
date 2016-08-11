package com.pillowapps.liqear.models;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.Spanned;
import android.widget.RemoteViews;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.HomeActivity;
import com.pillowapps.liqear.audio.MusicService;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.ButtonStateUtils;
import com.pillowapps.liqear.helpers.CompatIcs;
import com.pillowapps.liqear.helpers.TrackUtils;

import javax.inject.Inject;

import timber.log.Timber;

public class TrackNotificationModel {

    private Context context;
    private Timeline timeline;

    @Inject
    public TrackNotificationModel(Context context, Timeline timeline) {
        this.context = context;
        this.timeline = timeline;
    }

    public Notification create() {
        Track track = timeline.getCurrentTrack();
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            notification = createControllingNotification();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                CompatIcs.updateRemote(context, track);
            }
        } else {
            notification = createSimpleNotification(track);
        }
        return notification;
    }

    private Notification createSimpleNotification(Track track) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        Spanned ticker = Html.fromHtml(TrackUtils.getNotation(track));
        Spanned artist = Html.fromHtml(track.getArtist());
        Spanned title = Html.fromHtml(track.getTitle());
        Notification notification = builder.setContentIntent(pi)
                .setSmallIcon(R.drawable.ic_stat_liquid_bear_logotype_revision)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(artist)
                .build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        return notification;
    }

    private Notification createControllingNotification() {
        Track track = timeline.getCurrentTrack();
        if (track == null) {
            throw new NullPointerException("Track for notification must not be null");
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_stat_liquid_bear_logotype_revision)
                        .setTicker(Html.fromHtml(TrackUtils.getNotation(track)));
        final RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
        contentView.setTextViewText(R.id.title,
                Html.fromHtml(track.getTitle()));
        contentView.setTextViewText(R.id.artist,
                Html.fromHtml(track.getArtist()));
        mBuilder.setContent(contentView);
        Intent notificationIntent = new Intent(context, HomeActivity.class);

        int playButton = timeline.isPlaying()
                ? R.drawable.pause_button
                : R.drawable.play_button;
        Timber.d("timeline is playing = " + timeline.isPlaying());
        contentView.setImageViewResource(R.id.play_pause, playButton);

        ComponentName service = new ComponentName(context, MusicService.class);
        Intent playPause = new Intent(MusicService.ACTION_PLAY_PAUSE);
        playPause.setComponent(service);
        contentView.setOnClickPendingIntent(R.id.play_pause,
                PendingIntent.getService(context, 0, playPause, 0));

        Intent nextIntent = new Intent(MusicService.ACTION_NEXT);
        nextIntent.setComponent(service);
        contentView.setOnClickPendingIntent(R.id.next,
                PendingIntent.getService(context, 0, nextIntent, 0));

        Intent prevIntent = new Intent(MusicService.ACTION_PREV);
        prevIntent.setComponent(service);
        contentView.setOnClickPendingIntent(R.id.prev,
                PendingIntent.getService(context, 0, prevIntent, 0));

        Intent closeIntent = new Intent(MusicService.ACTION_CLOSE);
        closeIntent.setComponent(service);
        contentView.setOnClickPendingIntent(R.id.force_close,
                PendingIntent.getService(context, 0, closeIntent, 0));

        mBuilder.setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, 0));

        Bitmap bitmap = timeline.getAlbumCoverBitmap();
        if (bitmap == null) {
            contentView.setInt(R.id.album_cover_image_view, "setImageResource", R.drawable.lb_icon_white);
        } else {
            contentView.setImageViewBitmap(R.id.album_cover_image_view, bitmap);
        }

        mBuilder.setOngoing(true);
        mBuilder.setOnlyAlertOnce(true);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            final RemoteViews bigView = new RemoteViews(context.getPackageName(), R.layout.notification_big);
            bigView.setTextViewText(R.id.title,
                    Html.fromHtml(track.getTitle()));
            bigView.setTextViewText(R.id.artist,
                    Html.fromHtml(track.getArtist()));

            bigView.setImageViewResource(R.id.play_pause, playButton);

            playPause.setComponent(service);
            bigView.setOnClickPendingIntent(R.id.play_pause,
                    PendingIntent.getService(context, 0, playPause, 0));

            nextIntent.setComponent(service);
            bigView.setOnClickPendingIntent(R.id.next,
                    PendingIntent.getService(context, 0, nextIntent, 0));

            prevIntent.setComponent(service);
            bigView.setOnClickPendingIntent(R.id.prev,
                    PendingIntent.getService(context, 0, prevIntent, 0));

            closeIntent.setComponent(service);
            bigView.setOnClickPendingIntent(R.id.force_close,
                    PendingIntent.getService(context, 0, closeIntent, 0));

            Intent loveIntent = new Intent(MusicService.ACTION_LOVE);
            loveIntent.setComponent(service);
            int loveButton = ButtonStateUtils.getLoveButtonImage(track);
            bigView.setInt(R.id.love_button, "setImageResource", loveButton);
            bigView.setOnClickPendingIntent(R.id.love_button,
                    PendingIntent.getService(context, 0, loveIntent, 0));

            Intent addToVkFastIntent = new Intent(MusicService.ACTION_ADD_TO_VK_FAST);
            addToVkFastIntent.setComponent(service);
            if (track.isAddedToVk()) {
                bigView.setInt(R.id.add_to_vk_button, "setImageResource",
                        R.drawable.add_clicked);
                bigView.setOnClickPendingIntent(R.id.add_to_vk_button, null);
            } else {
                bigView.setInt(R.id.add_to_vk_button, "setImageResource",
                        R.drawable.add_button);
                bigView.setOnClickPendingIntent(R.id.add_to_vk_button,
                        PendingIntent.getService(context, 0, addToVkFastIntent, 0));
            }

            if (bitmap == null) {
                bigView.setInt(R.id.album_cover_image_view, "setImageResource", R.drawable.lb_icon_white);
            } else {
                bigView.setImageViewBitmap(R.id.album_cover_image_view, bitmap);
            }

            mBuilder.setStyle(new NotificationCompat.BigTextStyle());
            notification = mBuilder.build();
            notification.bigContentView = bigView;
        } else {
            notification = mBuilder.build();
        }
        return notification;
    }

}

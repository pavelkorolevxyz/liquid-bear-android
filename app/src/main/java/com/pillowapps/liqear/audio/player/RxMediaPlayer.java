package com.pillowapps.liqear.audio.player;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;

import rx.Observable;

public class RxMediaPlayer {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    private final Observable<ExoPlayer> mediaPlayerObservable;

    private RxMediaPlayer(Observable<ExoPlayer> mediaPlayerObservable) {
        this.mediaPlayerObservable = mediaPlayerObservable;
    }

    public static RxMediaPlayer create() {
        ExoPlayer exoPlayer = ExoPlayer.Factory.newInstance(1);
        return use(exoPlayer);
    }

    public static RxMediaPlayer use(@NonNull ExoPlayer mediaPlayer) {
        return new RxMediaPlayer(Observable.just(mediaPlayer));
    }

    public PreparedMediaPlayer from(Context context, String url) {
        return new PreparedMediaPlayer(mediaPlayerObservable.flatMap(mediaPlayer ->
                Observable.create(subscriber -> {
                    Log.d("url", url);
                    mediaPlayer.stop();
                    mediaPlayer.seekTo(0);
                    Uri uri = Uri.parse(url);

                    Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
                    DataSource dataSource = new DefaultUriDataSource(context, "RxMediaPlayer");

                    SampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);

                    MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, MediaCodecSelector.DEFAULT);
                    mediaPlayer.prepare(audioRenderer);
                    mediaPlayer.setPlayWhenReady(true);
                    subscriber.onNext(mediaPlayer);
                    subscriber.onCompleted();
                })));
    }

    public PreparedMediaPlayer preparedMediaPlayer() {
        return new PreparedMediaPlayer(mediaPlayerObservable);
    }
}

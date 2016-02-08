package com.pillowapps.liqear.audio.player;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;

import java.util.concurrent.TimeUnit;

import rx.Observable;

public class PreparedMediaPlayer {
    private static final int DEFAULT_TICK_PERIOD = 50;

    private final Observable<ExoPlayer> mediaPlayerObservable;

    public PreparedMediaPlayer(Observable<ExoPlayer> mediaPlayerObservable) {
        this.mediaPlayerObservable = mediaPlayerObservable;
    }

    public Observable<PlaybackInfo> withTicks() {
        return withTicks(DEFAULT_TICK_PERIOD);
    }

    public Observable<PlaybackInfo> withTicks(long updatePeriodMillis) {
        return mediaPlayerObservable.flatMap(mediaPlayer -> ticks(mediaPlayer, updatePeriodMillis).takeUntil(complete()));
    }

    private Observable<ExoPlayer> complete() {
        return mediaPlayerObservable.flatMap(mediaPlayer -> Observable.create(
                        subscriber -> mediaPlayer.addListener(new ExoPlayer.Listener() {
                            @Override
                            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                                if (playbackState == ExoPlayer.STATE_ENDED) {
                                    subscriber.onNext(mediaPlayer);
                                    subscriber.onCompleted();
                                }
                            }

                            @Override
                            public void onPlayWhenReadyCommitted() {

                            }

                            @Override
                            public void onPlayerError(ExoPlaybackException error) {

                            }
                        }))
        );
    }

    private Observable<PlaybackInfo> ticks(ExoPlayer mediaPlayer, long updatePeriodMillis) {
        return Observable.interval(updatePeriodMillis, TimeUnit.MILLISECONDS)
                .startWith(-1L)
                .map(tick -> new PlaybackInfo(mediaPlayer.getCurrentPosition(),
                        mediaPlayer.getDuration(),
                        mediaPlayer.getBufferedPosition()));
    }

    public Observable<ExoPlayer> seekTo(int progressMillis) {
        return mediaPlayerObservable.flatMap(mediaPlayer ->
                Observable.create(subscriber -> {
                    mediaPlayer.seekTo(progressMillis);
                    subscriber.onNext(mediaPlayer);
                    subscriber.onCompleted();
                }));
    }

    public Observable<ExoPlayer> play() {
        return mediaPlayerObservable.flatMap(mediaPlayer ->
                Observable.create(subscriber -> {
                    mediaPlayer.setPlayWhenReady(true);
                    subscriber.onNext(mediaPlayer);
                    subscriber.onCompleted();
                }));
    }

    public Observable<ExoPlayer> pause() {
        return mediaPlayerObservable.flatMap(mediaPlayer ->
                Observable.create(subscriber -> {
                    mediaPlayer.setPlayWhenReady(false);
                    subscriber.onNext(mediaPlayer);
                    subscriber.onCompleted();
                }));
    }

    public Observable<ExoPlayer> observable() {
        return mediaPlayerObservable;
    }
}

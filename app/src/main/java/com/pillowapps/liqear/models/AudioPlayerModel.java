package com.pillowapps.liqear.models;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.pillowapps.liqear.audio.VkAudioProvider;
import com.pillowapps.liqear.entities.Track;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class AudioPlayerModel {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    private Context context;

    private ExoPlayer audioPlayer;
    private VkAudioProvider vkAudioProvider;

    @Inject
    public AudioPlayerModel(Context context, ExoPlayer audioPlayer, VkAudioProvider vkAudioProvider) {
        this.context = context;
        this.audioPlayer = audioPlayer;
        this.vkAudioProvider = vkAudioProvider;
    }

    public Observable<Track> load(Track track) {
        return vkAudioProvider.getAudioUrl(track).flatMap(url -> Observable.create(subscriber -> {
            audioPlayer.stop();
            audioPlayer.seekTo(0);
            Uri uri = Uri.parse(url);

            Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
            DataSource dataSource = new DefaultUriDataSource(context, "Liquid Bear");

            SampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);

            MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, MediaCodecSelector.DEFAULT);
            audioPlayer.prepare(audioRenderer);
            Timber.d("track duration = " + getDuration());
            track.setDuration(getDuration());
            subscriber.onNext(track);
            subscriber.onCompleted();
        }));
    }

    public void play() {
        audioPlayer.setPlayWhenReady(true);
    }

    public void pause() {
        audioPlayer.setPlayWhenReady(false);
    }

    public int getDuration() {
        return (int) audioPlayer.getDuration();
    }

    public boolean isPlayReady() {
        return audioPlayer.getPlayWhenReady();
    }

    public int getCurrentPositionMillis() {
        return (int) audioPlayer.getCurrentPosition();
    }

    public int getCurrentBufferMillis() {
        return (int) audioPlayer.getBufferedPosition();
    }

    public void seekTo(int positionMillis) {
        audioPlayer.seekTo(positionMillis);
    }

    @Deprecated
    public int getCurrentPositionPercent() {
        return (int) (audioPlayer.getCurrentPosition() * 100 / audioPlayer.getDuration());
    }

    public Observable<Integer> addOnComplete() {
        return Observable.create(subscriber -> audioPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    subscriber.onNext(playbackState);
                }
                Timber.d("PlaybackState " + playbackState);
            }

            @Override
            public void onPlayWhenReadyCommitted() {
                // No op.
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                // No op.
            }
        }));
    }
}

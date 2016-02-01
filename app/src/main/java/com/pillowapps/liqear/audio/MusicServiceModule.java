package com.pillowapps.liqear.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import com.pillowapps.liqear.helpers.MusicServiceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MusicServiceModule {

    @Provides
    @NonNull
    @Singleton
    public Timeline provideTimeline() {
        return new Timeline();
    }

    @Provides
    @NonNull
    @Singleton
    public MediaPlayer provideMediaPlayer() {
        return new MediaPlayer();
    }

    @Provides
    @NonNull
    @Singleton
    public AudioManager provideAudioManager(Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Provides
    @NonNull
    @Singleton
    public MusicServiceManager provideMusicServiceManager() {
        return new MusicServiceManager();
    }

    @Provides
    @NonNull
    @Singleton
    public MediaPlayerManager provideMediaPlayerManager(Context context,
                                                        Timeline timeline,
                                                        MediaPlayer mediaPlayer,
                                                        AudioManager audioManager) {
        return new MediaPlayerManager(context, timeline, mediaPlayer, audioManager);
    }

    @Provides
    @NonNull
    @Singleton
    public RemoteControlManager provideRemoteControlManager(Context context,
                                                            Timeline timeline,
                                                            AudioManager audioManager) {
        return new RemoteControlManager(context, timeline, audioManager);
    }


}

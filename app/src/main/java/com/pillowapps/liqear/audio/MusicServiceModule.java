package com.pillowapps.liqear.audio;

import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.NonNull;

import com.google.android.exoplayer.ExoPlayer;
import com.pillowapps.liqear.helpers.MusicServiceManager;
import com.pillowapps.liqear.models.AudioPlayerModel;
import com.pillowapps.liqear.models.TickModel;
import com.pillowapps.liqear.models.lastfm.LastfmTrackModel;
import com.pillowapps.liqear.models.vk.VkAudioModel;
import com.pillowapps.liqear.models.vk.VkStatusModel;

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
    public ExoPlayer provideAudioPlayer() {
        return ExoPlayer.Factory.newInstance(1);
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
    public RemoteControlManager provideRemoteControlManager(Context context,
                                                            Timeline timeline,
                                                            AudioManager audioManager) {
        return new RemoteControlManager(context, timeline, audioManager);
    }

    @Provides
    @NonNull
    public AudioPlayerModel provideAudioPlayerModel(Context context, ExoPlayer player, VkAudioProvider vkAudioProvider) {
        return new AudioPlayerModel(context, player, vkAudioProvider);
    }

    @Provides
    @NonNull
    @Singleton
    public VkAudioProvider provideAudioProvider(VkAudioModel vkAudioModel) {
        return new VkAudioProvider(vkAudioModel);
    }

    @Provides
    @NonNull
    @Singleton
    public TickModel provideTickModel(LastfmTrackModel lastfmTrackModel, VkStatusModel vkStatusModel) {
        return new TickModel(lastfmTrackModel, vkStatusModel);
    }

}

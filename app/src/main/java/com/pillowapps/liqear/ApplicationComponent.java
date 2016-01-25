package com.pillowapps.liqear;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.activities.AuthActivity;
import com.pillowapps.liqear.activities.ImagePagerActivity;
import com.pillowapps.liqear.activities.PlaylistsActivity;
import com.pillowapps.liqear.activities.TextActivity;
import com.pillowapps.liqear.activities.modes.LastfmNeighboursActivity;
import com.pillowapps.liqear.activities.modes.LastfmRecommendationsActivity;
import com.pillowapps.liqear.activities.modes.SearchAlbumActivity;
import com.pillowapps.liqear.activities.modes.SearchArtistActivity;
import com.pillowapps.liqear.activities.modes.SearchLastfmUserActivity;
import com.pillowapps.liqear.activities.modes.SearchSimpleTrackActivity;
import com.pillowapps.liqear.activities.modes.SearchTagActivity;
import com.pillowapps.liqear.activities.modes.SetlistsResultActivity;
import com.pillowapps.liqear.activities.modes.VkAudioSearchActivity;
import com.pillowapps.liqear.activities.modes.VkFriendsActivity;
import com.pillowapps.liqear.activities.modes.VkGroupsActivity;
import com.pillowapps.liqear.activities.modes.VkRecommendationsActivity;
import com.pillowapps.liqear.activities.viewers.LastfmAlbumViewerActivity;
import com.pillowapps.liqear.activities.viewers.LastfmArtistViewerActivity;
import com.pillowapps.liqear.activities.viewers.LastfmChartsViewerActivity;
import com.pillowapps.liqear.activities.viewers.LastfmTagViewerActivity;
import com.pillowapps.liqear.activities.viewers.LastfmUserViewerActivity;
import com.pillowapps.liqear.activities.viewers.VkUserViewerActivity;
import com.pillowapps.liqear.audio.MusicService;
import com.pillowapps.liqear.audio.MusicServiceModule;
import com.pillowapps.liqear.fragments.HomeFragment;
import com.pillowapps.liqear.models.LastfmModelsModule;
import com.pillowapps.liqear.models.LiquidBearModelsModule;
import com.pillowapps.liqear.models.SetlistfmModelsModule;
import com.pillowapps.liqear.models.ShareModel;
import com.pillowapps.liqear.models.VkModelsModule;
import com.pillowapps.liqear.models.lastfm.LastfmDiscographyModel;
import com.pillowapps.liqear.models.lastfm.LastfmLibraryModel;
import com.pillowapps.liqear.models.lastfm.LastfmRecommendationsModel;
import com.pillowapps.liqear.network.LastfmApiModule;
import com.pillowapps.liqear.network.NetworkModule;
import com.pillowapps.liqear.network.SetlistfmApiModule;
import com.pillowapps.liqear.network.StateModule;
import com.pillowapps.liqear.network.StorageModule;
import com.pillowapps.liqear.network.VkApiModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        NetworkModule.class,
        StorageModule.class,
        StateModule.class,
        MusicServiceModule.class,
        LastfmApiModule.class,
        VkApiModule.class,
        SetlistfmApiModule.class,
        LiquidBearModelsModule.class,
        LastfmModelsModule.class,
        VkModelsModule.class,
        SetlistfmModelsModule.class
})
public interface ApplicationComponent {

    @NonNull
    HomeFragment.HomeFragmentComponent plus(@NonNull HomeFragment.HomeFragmentModule homeFragmentModule);

    void inject(@NonNull LBApplication application);

    void inject(@NonNull AuthActivity application);

    void inject(@NonNull MusicService application);

    void inject(@NonNull ImagePagerActivity imagePagerActivity);

    void inject(@NonNull TextActivity textActivity);

    void inject(@NonNull LastfmNeighboursActivity lastfmNeighboursActivity);

    void inject(@NonNull LastfmRecommendationsActivity lastfmRecommendationsActivity);

    void inject(@NonNull SearchAlbumActivity searchAlbumActivity);

    void inject(@NonNull SearchArtistActivity searchArtistActivity);

    void inject(@NonNull SearchLastfmUserActivity searchLastfmUserActivity);

    void inject(@NonNull SearchSimpleTrackActivity searchSimpleTrackActivity);

    void inject(@NonNull SearchTagActivity searchTagActivity);

    void inject(@NonNull SetlistsResultActivity setlistsResultActivity);

    void inject(@NonNull VkAudioSearchActivity vkAudioSearchActivity);

    void inject(@NonNull VkFriendsActivity vkFriendsActivity);

    void inject(@NonNull VkGroupsActivity vkGroupsActivity);

    void inject(@NonNull VkRecommendationsActivity vkRecommendationsActivity);

    void inject(@NonNull LastfmAlbumViewerActivity lastfmAlbumViewerActivity);

    void inject(@NonNull LastfmArtistViewerActivity lastfmArtistViewerActivity);

    void inject(@NonNull LastfmChartsViewerActivity lastfmChartsViewerActivity);

    void inject(@NonNull LastfmTagViewerActivity lastfmTagViewerActivity);

    void inject(@NonNull LastfmUserViewerActivity lastfmUserViewerActivity);

    void inject(@NonNull VkUserViewerActivity vkUserViewerActivity);

    void inject(@NonNull HomeFragment homeFragment);

    void inject(@NonNull ShareModel shareModel);

    void inject(@NonNull LastfmDiscographyModel lastfmDiscographyModel);

    void inject(@NonNull LastfmLibraryModel lastfmLibraryModel);

    void inject(@NonNull LastfmRecommendationsModel lastfmRecommendationsModel);

    void inject(@NonNull PlaylistsActivity playlistsActivity);

}


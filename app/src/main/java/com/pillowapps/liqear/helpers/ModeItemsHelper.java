package com.pillowapps.liqear.helpers;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.entities.Category;
import com.pillowapps.liqear.entities.Mode;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class ModeItemsHelper {

    @Inject
    public ModeItemsHelper() {
        // No op.
    }

    @NonNull
    public List<Mode> vkModes() {
        return Arrays.asList(
                new Mode(R.string.user_audio, R.drawable.ic_casette, Category.VK, R.id.vk_user_audio, false),
                new Mode(R.string.group, R.drawable.ic_friends_mode, Category.VK, R.id.vk_group, false),
                new Mode(R.string.vk_friends, R.drawable.ic_friends_mode, Category.VK, R.id.vk_friends, false),
                new Mode(R.string.vk_simple_search, R.drawable.ic_simple_search_mode, Category.VK, R.id.vk_search, false),
                new Mode(R.string.vk_albums, R.drawable.ic_mode_playlist, Category.VK, R.id.vk_albums, false),
                new Mode(R.string.recommendations, R.drawable.ic_recomendations_mode, Category.VK, R.id.vk_recommendations, false),
                new Mode(R.string.vk_wall, R.drawable.ic_wall_mode, Category.VK, R.id.vk_wall, false),
                new Mode(R.string.vk_favorites, R.drawable.ic_loved_mode, Category.VK, R.id.vk_favorites, false),
                new Mode(R.string.vk_feed, R.drawable.ic_wall_mode, Category.VK, R.id.vk_feed, false, false)
        );
    }

    @NonNull
    public List<Mode> lastfmModes() {
        return Arrays.asList(
                new Mode(R.string.loved, R.drawable.ic_loved_mode, Category.LAST_FM, R.id.lastfm_loved, true),
                new Mode(R.string.top_tracks, R.drawable.ic_casette, Category.LAST_FM, R.id.lastfm_top_tracks, true),
                new Mode(R.string.top_artists, R.drawable.ic_artists_mode, Category.LAST_FM, R.id.lastfm_top_artists, true),
                new Mode(R.string.charts, R.drawable.ic_charts_mode, Category.LAST_FM, R.id.lastfm_charts, false),
                new Mode(R.string.library, R.drawable.ic_library_mode, Category.LAST_FM, R.id.lastfm_library, true),
                new Mode(R.string.artist_radio, R.drawable.ic_artists_mode, Category.LAST_FM, R.id.lastfm_artist, false),
                new Mode(R.string.tag_radio, R.drawable.ic_tag_mode, Category.LAST_FM, R.id.lastfm_tag, false),
                new Mode(R.string.album, R.drawable.ic_audio_mode, Category.LAST_FM, R.id.lastfm_album, false),
//                new Mode(R.string.recommendations, R.drawable.ic_recomendations_mode, Category.LAST_FM, R.id.lastfm_recommendations, true, false),
                new Mode(R.string.radiomix, R.drawable.ic_mix_mode, Category.LAST_FM, R.id.lastfm_radiomix, true),
//                new Mode(R.string.neighbours, R.drawable.ic_friends_mode, Category.LAST_FM, R.id.lastfm_neighbours, true),
                new Mode(R.string.friends, R.drawable.ic_friends_mode, Category.LAST_FM, R.id.lastfm_friends, true),
                new Mode(R.string.recent, R.drawable.ic_mode_playlist, Category.LAST_FM, R.id.lastfm_recent, true, false)
        );
    }

    @NonNull
    public List<Mode> otherModes() {
        return Arrays.asList(
                new Mode(R.string.playlist_tab, R.drawable.playlist, Category.OTHER, R.id.playlists, false),
                new Mode(R.string.funkysouls, R.drawable.ic_funky_mode, Category.OTHER, R.id.other_funky, false),
                new Mode(R.string.alterportal, R.drawable.ic_alterportal_mode, Category.OTHER, R.id.other_alterportal, false),
                new Mode(R.string.setlist, R.drawable.ic_setlists_mode, Category.OTHER, R.id.other_setlists, false)
        );
    }

    @NonNull
    public List<Mode> localModes() {
        return Arrays.asList(
                new Mode(R.string.tracks, R.drawable.ic_casette, Category.LOCAL, R.id.local_tracks, false),
                new Mode(R.string.artist_radio, R.drawable.ic_artists_mode, Category.LOCAL, R.id.local_artists, false),
                new Mode(R.string.albums, R.drawable.ic_audio_mode, Category.LOCAL, R.id.local_albums, false)
        );
    }
}

package com.pillowapps.liqear.listeners;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.LastfmNeighboursActivity;
import com.pillowapps.liqear.activities.modes.LastfmRecommendationsActivity;
import com.pillowapps.liqear.activities.modes.LocalAlbumsActivity;
import com.pillowapps.liqear.activities.modes.LocalArtistsActivity;
import com.pillowapps.liqear.activities.modes.LocalTracksActivity;
import com.pillowapps.liqear.activities.modes.NewcomersActivity;
import com.pillowapps.liqear.activities.modes.SearchAlbumActivity;
import com.pillowapps.liqear.activities.modes.SearchArtistActivity;
import com.pillowapps.liqear.activities.modes.SearchLastfmUserActivity;
import com.pillowapps.liqear.activities.modes.SearchSimpleTrackActivity;
import com.pillowapps.liqear.activities.modes.SearchTagActivity;
import com.pillowapps.liqear.activities.modes.SetlistsActivity;
import com.pillowapps.liqear.activities.modes.VkFriendsActivity;
import com.pillowapps.liqear.activities.modes.VkGroupsActivity;
import com.pillowapps.liqear.activities.modes.VkRecommendationsActivity;
import com.pillowapps.liqear.activities.modes.viewers.LastfmChartsViewerActivity;
import com.pillowapps.liqear.activities.modes.viewers.LastfmUserViewerActivity;
import com.pillowapps.liqear.activities.modes.viewers.VkUserViewerActivity;
import com.pillowapps.liqear.activities.preferences.AuthActivity;
import com.pillowapps.liqear.activities.preferences.PreferencesActivity;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.fragments.HomeFragment;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;

public class OnModeListener {

    private final FragmentActivity context;
    private HomeFragment fragment;
    private AuthorizationInfoManager authorizationInfoManager;

    public OnModeListener(HomeFragment homeFragment, AuthorizationInfoManager authorizationInfoManager) {
        this.fragment = homeFragment;
        this.context = homeFragment.getActivity();
        this.authorizationInfoManager = authorizationInfoManager;
    }

    public void onItemClick(int modeId) {
//        if (mode.isNeedLastfm() && !authorizationInfoManager.isAuthorizedOnLastfm()) {
//            Toast.makeText(fragment, R.string.last_fm_not_authorized, Toast.LENGTH_SHORT).show();
//            return;
//        } else if ((mode.getCategory() == Category.VK || mode.getId() != R.id.lastfm_radiomix
//                || mode.getId() != R.id.lastfm_library)
//                && networkManager.isOnline() && !authorizationInfoManager.isAuthorizedOnVk()) {
//            Toast.makeText(fragment, R.string.vk_not_authorized, Toast.LENGTH_SHORT).show();
//            return;
//        } else if (mode.getCategory() != Category.LOCAL && !networkManager.isOnline()) {
//            Toast.makeText(fragment, R.string.no_internet, Toast.LENGTH_SHORT).show();
//            return;
//        }todo

        switch (modeId) {
            case R.id.lastfm_loved: {
                Intent intent = new Intent(context, LastfmUserViewerActivity.class);
                intent.putExtra(LastfmUserViewerActivity.USER,
                        new User(authorizationInfoManager.getLastfmName()));
                intent.putExtra(LastfmUserViewerActivity.TAB_INDEX,
                        LastfmUserViewerActivity.LOVED_INDEX);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] User Loved");
            }
            break;
            case R.id.lastfm_top_tracks: {
                Intent intent = new Intent(context, LastfmUserViewerActivity.class);
                intent.putExtra(LastfmUserViewerActivity.USER,
                        new User(authorizationInfoManager.getLastfmName()));
                intent.putExtra(LastfmUserViewerActivity.TAB_INDEX,
                        LastfmUserViewerActivity.TOP_TRACKS_INDEX);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Top Tracks");
            }
            break;
            case R.id.lastfm_top_artists: {
                Intent intent = new Intent(context, LastfmUserViewerActivity.class);
                intent.putExtra(LastfmUserViewerActivity.USER,
                        new User(authorizationInfoManager.getLastfmName()));
                intent.putExtra(LastfmUserViewerActivity.TAB_INDEX,
                        LastfmUserViewerActivity.TOP_ARTISTS_INDEX);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Top Artists");
            }
            break;
            case R.id.lastfm_charts: {
                Intent intent = new Intent(context, LastfmChartsViewerActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Charts");
            }
            break;
            case R.id.lastfm_library: {
//                fragment.openLibrary();todo
                sendAnalyticsModeClickEvent("[LAST.FM] Library");
            }
            break;
            case R.id.lastfm_artist: {
                Intent intent = new Intent(context, SearchArtistActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Artist search");
            }
            break;
            case R.id.lastfm_tag: {
                Intent intent = new Intent(context, SearchTagActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Tag search");
            }
            break;
            case R.id.lastfm_album: {
                Intent intent = new Intent(context, SearchAlbumActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Album search");
            }
            break;
            case R.id.lastfm_recommendations: {
                Intent intent = new Intent(context, LastfmRecommendationsActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Recommended");
            }
            break;
            case R.id.lastfm_radiomix: {
//                fragment.openRadiomix();todo
                sendAnalyticsModeClickEvent("[LAST.FM] Radiomix");
            }
            break;
            case R.id.lastfm_neighbours: {
                Intent intent = new Intent(context, LastfmNeighboursActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Neighbours");
            }
            break;
            case R.id.lastfm_friends: {
                Intent intent = new Intent(context, SearchLastfmUserActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Friends");
            }
            break;
            case R.id.lastfm_recent: {
                Intent intent = new Intent(context, LastfmUserViewerActivity.class);
                intent.putExtra(LastfmUserViewerActivity.USER,
                        new User(authorizationInfoManager.getLastfmName()));
                intent.putExtra(LastfmUserViewerActivity.TAB_INDEX,
                        LastfmUserViewerActivity.RECENT_INDEX);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Recent");
            }
            break;
            case R.id.vk_user_audio: {
                Intent intent = new Intent(context, VkUserViewerActivity.class);
                intent.putExtra(VkUserViewerActivity.USER,
                        new User(authorizationInfoManager.getVkName(),
                                authorizationInfoManager.getVkUserId()));
                intent.putExtra(VkUserViewerActivity.TAB_INDEX,
                        VkUserViewerActivity.USER_AUDIO_INDEX);
                intent.putExtra(VkUserViewerActivity.YOU_MODE, true);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] User Audio");
            }
            break;
            case R.id.vk_group: {
                Intent intent = new Intent(context, VkGroupsActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Group");
            }
            break;
            case R.id.vk_friends: {
                Intent intent = new Intent(context, VkFriendsActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Friends");
            }
            break;
            case R.id.vk_search: {
                Intent intent = new Intent(context, SearchSimpleTrackActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Search");
            }
            break;
            case R.id.vk_albums: {
                Intent intent = new Intent(context, VkUserViewerActivity.class);
                intent.putExtra(VkUserViewerActivity.USER,
                        new User(authorizationInfoManager.getVkName(),
                                authorizationInfoManager.getVkUserId()));
                intent.putExtra(VkUserViewerActivity.TAB_INDEX, VkUserViewerActivity.ALBUM_INDEX);
                intent.putExtra(VkUserViewerActivity.YOU_MODE, true);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Albums");
            }
            break;
            case R.id.vk_recommendations: {
                Intent intent = new Intent(context, VkRecommendationsActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Recommendations");
            }
            break;
            case R.id.vk_wall: {
                Intent intent = new Intent(context, VkUserViewerActivity.class);
                intent.putExtra(VkUserViewerActivity.USER,
                        new User(authorizationInfoManager.getVkName(),
                                authorizationInfoManager.getVkUserId()));
                intent.putExtra(VkUserViewerActivity.TAB_INDEX, VkUserViewerActivity.WALL_INDEX);
                intent.putExtra(VkUserViewerActivity.YOU_MODE, true);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Wall");
            }
            break;
            case R.id.vk_favorites: {
                Intent intent = new Intent(context, VkUserViewerActivity.class);
                intent.putExtra(VkUserViewerActivity.USER,
                        new User(authorizationInfoManager.getVkName(),
                                authorizationInfoManager.getVkUserId()));
                intent.putExtra(VkUserViewerActivity.TAB_INDEX, VkUserViewerActivity.FAVORITES);
                intent.putExtra(VkUserViewerActivity.YOU_MODE, true);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Favorites");
            }
            break;
            case R.id.vk_feed: {
                Intent intent = new Intent(context, VkUserViewerActivity.class);
                intent.putExtra(VkUserViewerActivity.USER,
                        new User(authorizationInfoManager.getVkName(),
                                authorizationInfoManager.getVkUserId()));
                intent.putExtra(VkUserViewerActivity.TAB_INDEX, VkUserViewerActivity.NEWS_FEED);
                intent.putExtra(VkUserViewerActivity.YOU_MODE, true);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Feed");
            }
            break;
            case R.id.other_funky: {
                Intent intent = new Intent(context, NewcomersActivity.class);
                intent.putExtra(NewcomersActivity.MODE,
                        NewcomersActivity.Mode.FUNKYSOULS);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[OTHER] FunkySouls");
            }
            break;
            case R.id.other_alterportal: {
                Intent intent = new Intent(context, NewcomersActivity.class);
                intent.putExtra(NewcomersActivity.MODE,
                        NewcomersActivity.Mode.ALTERPORTAL);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[OTHER] Alterportal");
            }
            break;
            case R.id.other_setlists: {
                Intent intent = new Intent(context, SetlistsActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[OTHER] Setlists");
            }
            break;
            case R.id.local_artists: {
                Intent intent = new Intent(context, LocalArtistsActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LOCAL] Artists");
            }
            break;
            case R.id.local_tracks: {
                Intent intent = new Intent(context, LocalTracksActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LOCAL] Tracks");
            }
            break;
            case R.id.local_albums: {
                Intent intent = new Intent(context, LocalAlbumsActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LOCAL] Albums");
            }
            break;
            case R.id.auth: {
                fragment.startActivity(AuthActivity.startIntent(context));
            }
            break;
            case R.id.settings: {
                fragment.startActivity(PreferencesActivity.startIntent(context));
            }
            break;
            case R.id.vk_auth: {
                fragment.startActivity(AuthActivity.startIntent(context).putExtra(AuthActivity.OPEN_PAGE, AuthActivity.VK_INDEX));
            }
            break;
            case R.id.lastfm_auth: {
                fragment.startActivity(AuthActivity.startIntent(context).putExtra(AuthActivity.OPEN_PAGE, AuthActivity.LASTFM_INDEX));
            }
            break;
            default:
                break;
        }
    }

    private void sendAnalyticsModeClickEvent(String mode) {
        EasyTracker easyTracker = EasyTracker.getInstance(context);
        easyTracker.send(MapBuilder
                .createEvent("ui_action", "mode_click", mode, null)
                .build()
        );
    }
}
package com.pillowapps.liqear.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

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
import com.pillowapps.liqear.entities.Category;
import com.pillowapps.liqear.entities.ListItem;
import com.pillowapps.liqear.entities.Mode;
import com.pillowapps.liqear.entities.ModeEnum;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.fragments.HomeFragment;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.NetworkModel;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapterWrapper;

public class OnModeClickListener implements android.widget.AdapterView.OnItemClickListener {

    private final HomeFragment fragment;
    private final Context context;

    private AuthorizationInfoManager authorizationInfoManager;
    private NetworkModel networkModel;

    public OnModeClickListener(HomeFragment fragment, AuthorizationInfoManager authorizationInfoManager, NetworkModel networkModel) {
        this.fragment = fragment;
        this.authorizationInfoManager = authorizationInfoManager;
        this.networkModel = networkModel;
        this.context = fragment.getContext();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Mode mode;
        if (adapterView.getAdapter() instanceof StickyGridHeadersBaseAdapterWrapper) {
            StickyGridHeadersBaseAdapterWrapper adapter =
                    ((StickyGridHeadersBaseAdapterWrapper) adapterView.getAdapter());
            StickyGridHeadersBaseAdapter wrappedAdapter = adapter.getWrappedAdapter();
            mode = (Mode) wrappedAdapter.getItem(position);
        } else {
            Object itemAtPosition = adapterView.getItemAtPosition(position);
            if (itemAtPosition instanceof ListItem) {
                mode = ((ListItem) itemAtPosition).getMode();
            } else {
                return;
            }
        }

        if (mode.isNeedLastfm() && !authorizationInfoManager.isAuthorizedOnLastfm()) {
            Toast.makeText(context, R.string.last_fm_not_authorized, Toast.LENGTH_SHORT).show();
            return;
        } else if ((mode.getCategory() == Category.VK || mode.getModeEnum() == ModeEnum.RADIOMIX
                || mode.getModeEnum() == ModeEnum.LIBRARY)
                && networkModel.isOnline() && !authorizationInfoManager.isAuthorizedOnVk()) {
            Toast.makeText(context, R.string.vk_not_authorized, Toast.LENGTH_SHORT).show();
            return;
        } else if (mode.getCategory() != Category.LOCAL && !networkModel.isOnline()) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        switch (mode.getModeEnum()) {
            case LOVED: {
                Intent intent = new Intent(context, LastfmUserViewerActivity.class);
                intent.putExtra(LastfmUserViewerActivity.USER,
                        new User(authorizationInfoManager.getLastfmName()));
                intent.putExtra(LastfmUserViewerActivity.TAB_INDEX,
                        LastfmUserViewerActivity.LOVED_INDEX);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] User Loved");
            }
            break;
            case TOP_TRACKS: {
                Intent intent = new Intent(context, LastfmUserViewerActivity.class);
                intent.putExtra(LastfmUserViewerActivity.USER,
                        new User(authorizationInfoManager.getLastfmName()));
                intent.putExtra(LastfmUserViewerActivity.TAB_INDEX,
                        LastfmUserViewerActivity.TOP_TRACKS_INDEX);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Top Tracks");
            }
            break;
            case TOP_ARTISTS: {
                Intent intent = new Intent(context, LastfmUserViewerActivity.class);
                intent.putExtra(LastfmUserViewerActivity.USER,
                        new User(authorizationInfoManager.getLastfmName()));
                intent.putExtra(LastfmUserViewerActivity.TAB_INDEX,
                        LastfmUserViewerActivity.TOP_ARTISTS_INDEX);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Top Artists");
            }
            break;
            case CHARTS: {
                Intent intent = new Intent(context, LastfmChartsViewerActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Charts");
            }
            break;
            case LIBRARY: {
                fragment.openLibrary();
                sendAnalyticsModeClickEvent("[LAST.FM] Library");
            }
            break;
            case ARTIST_RADIO: {
                Intent intent = new Intent(context, SearchArtistActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Artist search");
            }
            break;
            case TAG_RADIO: {
                Intent intent = new Intent(context, SearchTagActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Tag search");
            }
            break;
            case ALBUM_RADIO: {
                Intent intent = new Intent(context, SearchAlbumActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Album search");
            }
            break;
            case RECOMMENDATIONS: {
                Intent intent = new Intent(context, LastfmRecommendationsActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Recommended");
            }
            break;
            case RADIOMIX: {
                fragment.openRadiomix();
                sendAnalyticsModeClickEvent("[LAST.FM] Radiomix");
            }
            break;
            case NEIGHBOURS: {
                Intent intent = new Intent(context, LastfmNeighboursActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Neighbours");
            }
            break;
            case FRIENDS_LAST: {
                Intent intent = new Intent(context, SearchLastfmUserActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Friends");
            }
            break;
            case RECENT_LAST: {
                Intent intent = new Intent(context, LastfmUserViewerActivity.class);
                intent.putExtra(LastfmUserViewerActivity.USER,
                        new User(authorizationInfoManager.getLastfmName()));
                intent.putExtra(LastfmUserViewerActivity.TAB_INDEX,
                        LastfmUserViewerActivity.RECENT_INDEX);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Recent");
            }
            break;
            case USER_AUDIO_VK: {
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
            case GROUP_VK: {
                Intent intent = new Intent(context, VkGroupsActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Group");
            }
            break;
            case FRIENDS_VK: {
                Intent intent = new Intent(context, VkFriendsActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Friends");
            }
            break;
            case SEARCH_VK: {
                Intent intent = new Intent(context, SearchSimpleTrackActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Search");
            }
            break;
            case ALBUMS_VK: {
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
            case RECOMMENDATIONS_VK: {
                Intent intent = new Intent(context, VkRecommendationsActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Recommendations");
            }
            break;
            case WALL_VK: {
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
            case FAVORITES_VK: {
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
            case FEED_VK: {
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
            case FUNKY: {
                Intent intent = new Intent(context, NewcomersActivity.class);
                intent.putExtra(NewcomersActivity.MODE,
                        NewcomersActivity.Mode.FUNKYSOULS);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[OTHER] FunkySouls");
            }
            break;
            case ALTERPORTAL: {
                Intent intent = new Intent(context, NewcomersActivity.class);
                intent.putExtra(NewcomersActivity.MODE,
                        NewcomersActivity.Mode.ALTERPORTAL);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[OTHER] Alterportal");
            }
            break;
            case SETLIST: {
                Intent intent = new Intent(context, SetlistsActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[OTHER] Setlists");
            }
            break;
            case LOCAL_ARTISTS: {
                Intent intent = new Intent(context, LocalArtistsActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LOCAL] Artists");
            }
            break;
            case LOCAL_TRACKS: {
                Intent intent = new Intent(context, LocalTracksActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LOCAL] Tracks");
            }
            break;
            case LOCAL_ALBUMS: {
                Intent intent = new Intent(context, LocalAlbumsActivity.class);
                fragment.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LOCAL] Albums");
            }
            break;
            default:
                break;
        }
    }

    private void sendAnalyticsModeClickEvent(String mode) {
        EasyTracker easyTracker = EasyTracker.getInstance(fragment.getActivity());
        easyTracker.send(MapBuilder
                        .createEvent("ui_action", "mode_click", mode, null)
                        .build()
        );
    }
}
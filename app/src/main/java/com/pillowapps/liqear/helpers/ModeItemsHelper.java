package com.pillowapps.liqear.helpers;

import android.content.Context;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.callbacks.UpdateAdapterCallback;
import com.pillowapps.liqear.entities.Category;
import com.pillowapps.liqear.entities.Header;
import com.pillowapps.liqear.entities.Item;
import com.pillowapps.liqear.entities.ListItem;
import com.pillowapps.liqear.entities.Mode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModeItemsHelper {
    private boolean editMode;
    private AuthorizationInfoManager authorizationInfoManager;
    public static final List<Mode> allModes = Arrays.asList(
            //Vk
            new Mode(R.string.user_audio, R.drawable.ic_casette, Category.VK, R.id.vk_user_audio, false),
            new Mode(R.string.group, R.drawable.ic_friends_mode, Category.VK, R.id.vk_group, false),
            new Mode(R.string.vk_friends, R.drawable.ic_friends_mode, Category.VK, R.id.vk_friends, false),
            new Mode(R.string.vk_simple_search, R.drawable.ic_simple_search_mode, Category.VK, R.id.vk_search, false),
            new Mode(R.string.vk_albums, R.drawable.ic_mode_playlist, Category.VK, R.id.vk_albums, false),
            new Mode(R.string.recommendations, R.drawable.ic_recomendations_mode, Category.VK, R.id.vk_recommendations, false),
            new Mode(R.string.vk_wall, R.drawable.ic_wall_mode, Category.VK, R.id.vk_wall, false),
            new Mode(R.string.vk_favorites, R.drawable.ic_loved_mode, Category.VK, R.id.vk_favorites, false),
            new Mode(R.string.vk_feed, R.drawable.ic_wall_mode, Category.VK, R.id.vk_feed, false, false),
            //Lastfm
            new Mode(R.string.loved, R.drawable.ic_loved_mode, Category.LAST_FM, R.id.lastfm_loved, true),
            new Mode(R.string.top_tracks, R.drawable.ic_casette, Category.LAST_FM, R.id.lastfm_top_tracks, true),
            new Mode(R.string.top_artists, R.drawable.ic_artists_mode, Category.LAST_FM, R.id.lastfm_top_artists, true),
            new Mode(R.string.charts, R.drawable.ic_charts_mode, Category.LAST_FM, R.id.lastfm_charts, false),
            new Mode(R.string.library, R.drawable.ic_library_mode, Category.LAST_FM, R.id.lastfm_library, true),
            new Mode(R.string.artist_radio, R.drawable.ic_artists_mode, Category.LAST_FM, R.id.lastfm_artist, false),
            new Mode(R.string.tag_radio, R.drawable.ic_tag_mode, Category.LAST_FM, R.id.lastfm_tag, false),
            new Mode(R.string.album, R.drawable.ic_audio_mode, Category.LAST_FM, R.id.lastfm_album, false),
//            new Mode(R.string.recommendations, R.drawable.ic_recomendations_mode, Category.LAST_FM, ModeEnum.RECOMMENDATIONS, true, false),
            new Mode(R.string.radiomix, R.drawable.ic_mix_mode, Category.LAST_FM, R.id.lastfm_radiomix, true),
//            new Mode(R.string.neighbours, R.drawable.ic_friends_mode, Category.LAST_FM, ModeEnum.NEIGHBOURS, true),
            new Mode(R.string.friends, R.drawable.ic_friends_mode, Category.LAST_FM, R.id.lastfm_friends, true),
            new Mode(R.string.recent, R.drawable.ic_mode_playlist, Category.LAST_FM, R.id.lastfm_recent, true, false),
            //Other
            new Mode(R.string.funkysouls, R.drawable.ic_funky_mode, Category.OTHER, R.id.other_funky, false),
            new Mode(R.string.alterportal, R.drawable.ic_alterportal_mode, Category.OTHER, R.id.other_alterportal, false),
            new Mode(R.string.setlist, R.drawable.ic_setlists_mode, Category.OTHER, R.id.other_setlists, false),
            //Local
            new Mode(R.string.tracks, R.drawable.ic_casette, Category.LOCAL, R.id.local_tracks, false),
            new Mode(R.string.artist_radio, R.drawable.ic_artists_mode, Category.LOCAL, R.id.local_artists, false),
            new Mode(R.string.albums, R.drawable.ic_audio_mode, Category.LOCAL, R.id.local_albums, false)
    );
    private List<Mode> modes = new ArrayList<>(allModes.size());
    private List<Item> items;
    private List<Category> categories = new ArrayList<>(4);
    private List<Integer> itemsPerCategory = new ArrayList<>(4);
    private Context context;
    private NetworkManager networkManager;

    public ModeItemsHelper(Context context, AuthorizationInfoManager authorizationInfoManager, NetworkManager networkManager) {
        this.context = context;
        this.authorizationInfoManager = authorizationInfoManager;
        this.networkManager = networkManager;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isModeEnabled(Mode mode) {
        final boolean modeVisible = mode.isVisible();
        boolean isModeLocal = mode.getCategory() == Category.LOCAL;
        boolean areBothAuthorized = authorizationInfoManager.isAuthorizedOnVk()
                && authorizationInfoManager.isAuthorizedOnLastfm();
        boolean isOnlyVkModeAndAuthorized = authorizationInfoManager.isAuthorizedOnVk()
                && !mode.isNeedLastfm();
        boolean isModeForLastfmAuthorized = authorizationInfoManager.isAuthorizedOnLastfm()
                &&
                mode.getCategory() != Category.VK
                &&
                mode.getId() != R.id.lastfm_radiomix
                &&
                mode.getId() != R.id.lastfm_library;
        boolean isLastfmModeNotNeededAuth = !mode.isNeedLastfm() && mode.getCategory() != Category.VK;
        if (!modeVisible) {
            return false;
        }

        if (isModeLocal) {
            return true;
        }

        if (!networkManager.isOnline()) {
            return false;
        }

        return areBothAuthorized || isOnlyVkModeAndAuthorized || isModeForLastfmAuthorized || isLastfmModeNotNeededAuth;
    }

    public List<Item> createItemsList(UpdateAdapterCallback callback) {
        calcNewModesList();
        items = new ArrayList<>();
        Mode prev;
        if (modes.size() > 0) {
            prev = modes.get(0);
            items.add(new Header(context, prev.getCategoryTitle()));
            items.add(new ListItem(context, prev, callback, this));
        }
        for (int i = 1; i < modes.size(); i++) {
            prev = modes.get(i - 1);
            Mode current = modes.get(i);
            if (prev.getCategory() != current.getCategory()) {
                items.add(new Header(context,
                        current.getCategoryTitle()));
            }
            items.add(new ListItem(context, current, callback, this));
        }
        return items;
    }

    public void calcNewModesList() {
        modes.clear();
        categories.clear();
        itemsPerCategory.clear();
        for (Mode currentMode : allModes) {
            modes.add(currentMode);
            Category category = currentMode.getCategory();
            if (!categories.contains(category)) {
                itemsPerCategory.add(0);
                categories.add(category);
            }
            itemsPerCategory.set(categories.size() - 1, itemsPerCategory.get(categories.size() - 1) + 1);
        }
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Mode> getModes() {
        calcNewModesList();
        return modes;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Integer> getItemsPerCategory() {
        return itemsPerCategory;
    }
}

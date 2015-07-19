package com.pillowapps.liqear.helpers;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.UpdateAdapterCallback;
import com.pillowapps.liqear.entities.Category;
import com.pillowapps.liqear.entities.Header;
import com.pillowapps.liqear.entities.Item;
import com.pillowapps.liqear.entities.ListItem;
import com.pillowapps.liqear.entities.Mode;
import com.pillowapps.liqear.entities.ModeEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModeItemsHelper {
    private static boolean editMode;
    private List<Mode> allModes = Arrays.asList(
            //Vk
            new Mode(R.string.user_audio, R.drawable.ic_casette, Category.VK, ModeEnum.USER_AUDIO_VK, false),
            new Mode(R.string.group, R.drawable.ic_friends_mode, Category.VK, ModeEnum.GROUP_VK, false),
            new Mode(R.string.vk_friends, R.drawable.ic_friends_mode, Category.VK, ModeEnum.FRIENDS_VK, false),
            new Mode(R.string.vk_simple_search, R.drawable.ic_simple_search_mode, Category.VK, ModeEnum.SEARCH_VK, false),
            new Mode(R.string.vk_albums, R.drawable.ic_mode_playlist, Category.VK, ModeEnum.ALBUMS_VK, false),
            new Mode(R.string.recommendations, R.drawable.ic_recomendations_mode, Category.VK, ModeEnum.RECOMMENDATIONS_VK, false),
            new Mode(R.string.vk_wall, R.drawable.ic_wall_mode, Category.VK, ModeEnum.WALL_VK, false),
            new Mode(R.string.vk_favorites, R.drawable.ic_loved_mode, Category.VK, ModeEnum.FAVORITES_VK, false),
            new Mode(R.string.vk_feed, R.drawable.ic_wall_mode, Category.VK, ModeEnum.FEED_VK, false, false),
            //Lastfm
            new Mode(R.string.loved, R.drawable.ic_loved_mode, Category.LAST_FM, ModeEnum.LOVED, true),
            new Mode(R.string.top_tracks, R.drawable.ic_casette, Category.LAST_FM, ModeEnum.TOP_TRACKS, true),
            new Mode(R.string.top_artists, R.drawable.ic_artists_mode, Category.LAST_FM, ModeEnum.TOP_ARTISTS, true),
            new Mode(R.string.charts, R.drawable.ic_charts_mode, Category.LAST_FM, ModeEnum.CHARTS, false),
            new Mode(R.string.library, R.drawable.ic_library_mode, Category.LAST_FM, ModeEnum.LIBRARY, true),
            new Mode(R.string.artist_radio, R.drawable.ic_artists_mode, Category.LAST_FM, ModeEnum.ARTIST_RADIO, false),
            new Mode(R.string.tag_radio, R.drawable.ic_tag_mode, Category.LAST_FM, ModeEnum.TAG_RADIO, false),
            new Mode(R.string.album, R.drawable.ic_audio_mode, Category.LAST_FM, ModeEnum.ALBUM_RADIO, false),
            new Mode(R.string.recommendations, R.drawable.ic_recomendations_mode, Category.LAST_FM, ModeEnum.RECOMMENDATIONS, true),
            new Mode(R.string.radiomix, R.drawable.ic_mix_mode, Category.LAST_FM, ModeEnum.RADIOMIX, true),
            new Mode(R.string.neighbours, R.drawable.ic_friends_mode, Category.LAST_FM, ModeEnum.NEIGHBOURS, true),
            new Mode(R.string.friends, R.drawable.ic_friends_mode, Category.LAST_FM, ModeEnum.FRIENDS_LAST, true),
            new Mode(R.string.recent, R.drawable.ic_mode_playlist, Category.LAST_FM, ModeEnum.RECENT_LAST, true, false),
            //Other
            new Mode(R.string.funkysouls, R.drawable.ic_funky_mode, Category.OTHER, ModeEnum.FUNKY, false),
            new Mode(R.string.alterportal, R.drawable.ic_alterportal_mode, Category.OTHER, ModeEnum.ALTERPORTAL, false),
            new Mode(R.string.setlist, R.drawable.ic_setlists_mode, Category.OTHER, ModeEnum.SETLIST, false),
            //Local
            new Mode(R.string.tracks, R.drawable.ic_casette, Category.LOCAL, ModeEnum.LOCAL_TRACKS, false),
            new Mode(R.string.artist_radio, R.drawable.ic_artists_mode, Category.LOCAL, ModeEnum.LOCAL_ARTISTS, false),
            new Mode(R.string.albums, R.drawable.ic_audio_mode, Category.LOCAL, ModeEnum.LOCAL_ALBUMS, false)
    );
    private List<Mode> modes = new ArrayList<>(allModes.size());
    private List<Item> items;
    private List<Category> categories = new ArrayList<>(4);
    private List<Integer> itemsPerCategory = new ArrayList<>(4);

    public static boolean isEditMode() {
        return editMode;
    }

    public static void setEditMode(boolean editMode) {
        ModeItemsHelper.editMode = editMode;
    }

    public static boolean isModeEnabled(Mode mode) {
        final boolean modeVisible = mode.isVisible();
        boolean isModeLocal = mode.getCategory() == Category.LOCAL;
        boolean areBothAuthorized = AuthorizationInfoManager.isAuthorizedOnVk()
                && AuthorizationInfoManager.isAuthorizedOnLastfm();
        boolean isOnlyVkModeAndAuthorized = AuthorizationInfoManager.isAuthorizedOnVk()
                && !mode.isNeedLastfm();
        boolean isModeForLastfmAuthorized = AuthorizationInfoManager.isAuthorizedOnLastfm()
                &&
                mode.getCategory() != Category.VK
                &&
                mode.getModeEnum() != ModeEnum.RADIOMIX
                &&
                mode.getModeEnum() != ModeEnum.LIBRARY;
        boolean isLastfmModeNotNeededAuth = !mode.isNeedLastfm() && mode.getCategory() != Category.VK;
        return modeVisible
                && (isModeLocal
                || (((areBothAuthorized
                || isOnlyVkModeAndAuthorized
                || isModeForLastfmAuthorized)
                || isLastfmModeNotNeededAuth)
                && Utils.isOnline()));
    }

    public List<Item> createItemsList(UpdateAdapterCallback callback) {
        calcNewModesList();
        items = new ArrayList<>();
        Mode prev;
        if (modes.size() > 0) {
            prev = modes.get(0);
            items.add(new Header(LBApplication.getAppContext(), prev.getCategoryTitle()));
            items.add(new ListItem(LBApplication.getAppContext(), prev, callback));
        }
        for (int i = 1; i < modes.size(); i++) {
            prev = modes.get(i - 1);
            Mode current = modes.get(i);
            if (prev.getCategory() != current.getCategory()) {
                items.add(new Header(LBApplication.getAppContext(),
                        current.getCategoryTitle()));
            }
            items.add(new ListItem(LBApplication.getAppContext(), current, callback));
        }
        return items;
    }

    public void calcNewModesList() {
        modes.clear();
        categories.clear();
        itemsPerCategory.clear();
        for (Mode currentMode : allModes) {
            if (SharedPreferencesManager.getModePreferences().getBoolean(Constants.MODE_VISIBLE
                    + currentMode.getModeEnum(), currentMode.isVisible()) || isEditMode()) {
                modes.add(currentMode);
                Category category = currentMode.getCategory();
                if (!categories.contains(category)) {
                    itemsPerCategory.add(0);
                    categories.add(category);
                }
                itemsPerCategory.set(categories.size() - 1, itemsPerCategory.get(categories.size() - 1) + 1);
            }
        }
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Mode> getModes() {
        return modes;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Integer> getItemsPerCategory() {
        return itemsPerCategory;
    }
}

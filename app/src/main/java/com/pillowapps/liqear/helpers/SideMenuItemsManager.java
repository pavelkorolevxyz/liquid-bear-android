package com.pillowapps.liqear.helpers;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.mikepenz.materialdrawer.model.AbstractDrawerItem;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.entities.Category;
import com.pillowapps.liqear.entities.Mode;
import com.pillowapps.liqear.views.ProfileHeaderDrawerItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SideMenuItemsManager {

    private AuthorizationInfoManager authorizationInfoManager;
    private NetworkManager networkManager;
    private ModeItemsHelper modeItemsHelper;

    @Inject
    public SideMenuItemsManager(ModeItemsHelper modeItemsHelper, AuthorizationInfoManager authorizationInfoManager,
                                NetworkManager networkManager) {
        this.modeItemsHelper = modeItemsHelper;
        this.authorizationInfoManager = authorizationInfoManager;
        this.networkManager = networkManager;
    }

    @NonNull
    public ArrayList<IDrawerItem> items() {
        ArrayList<IDrawerItem> items = new ArrayList<>();
        items.add(profileHeader());
        items.addAll(modes());
        items.addAll(footer());
        return items;
    }

    IDrawerItem profileHeader() {
        return new ProfileHeaderDrawerItem();
    }

    List<AbstractDrawerItem> modes() {
        List<AbstractDrawerItem> items = new ArrayList<>();
        items.addAll(fromModeList(modeItemsHelper.vkModes()));
        items.addAll(fromModeList(modeItemsHelper.lastfmModes()));
        items.addAll(fromModeList(modeItemsHelper.otherModes()));
        items.addAll(fromModeList(modeItemsHelper.localModes()));
        items.add(new DividerDrawerItem());
        return items;
    }

    List<AbstractDrawerItem> fromModeList(List<Mode> modes) {
        List<AbstractDrawerItem> items = new ArrayList<>();
        if (modes == null || modes.isEmpty()) {
            return items;
        }
        items.add(new SecondaryDrawerItem().withName(modes.get(0).getCategoryTitle()));
        for (Mode mode : modes) {
            PrimaryDrawerItem primaryDrawerItem = primary(mode.getTitle(), mode.getIcon())
                    .withEnabled(isModeEnabled(mode))
                    .withIdentifier(mode.getId());
            items.add(primaryDrawerItem);
        }
        return items;
    }

    List<AbstractDrawerItem> footer() {
        List<AbstractDrawerItem> items = new ArrayList<>();
        items.add(primary(R.string.authorizations, R.drawable.settings_normal).withIdentifier(R.id.auth));
        items.add(primary(R.string.settings, R.drawable.settings_normal).withIdentifier(R.id.settings));
        return items;
    }

    private PrimaryDrawerItem primary(@StringRes int name, @DrawableRes int icon) {
        return new PrimaryDrawerItem().withName(name).withIcon(icon)
                .withIconColorRes(R.color.accent)
                .withIconTintingEnabled(true)
                .withSelectable(false);
    }


    public boolean isModeEnabled(Mode mode) {
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

        if (isModeLocal) {
            return true;
        }

        if (!networkManager.isOnline()) {
            return false;
        }

        return areBothAuthorized || isOnlyVkModeAndAuthorized || isModeForLastfmAuthorized || isLastfmModeNotNeededAuth;
    }
}

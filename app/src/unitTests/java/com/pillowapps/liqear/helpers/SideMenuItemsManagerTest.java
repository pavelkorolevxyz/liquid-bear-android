package com.pillowapps.liqear.helpers;

import com.mikepenz.materialdrawer.model.AbstractDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.pillowapps.liqear.entities.Category;
import com.pillowapps.liqear.entities.Mode;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SideMenuItemsManagerTest {

    private SideMenuItemsManager sideMenuItemsManager;
    private AuthorizationInfoManager authManager;
    private NetworkManager networkManager;
    private ModeItemsHelper modeItemsHelper;

    @Before
    public void setUp() {
        authManager = mock(AuthorizationInfoManager.class);
        networkManager = mock(NetworkManager.class);
        modeItemsHelper = mock(ModeItemsHelper.class);
        sideMenuItemsManager = new SideMenuItemsManager(modeItemsHelper, authManager, networkManager);
    }

    @Test
    public void getItems_shouldReturnData() {
        ArrayList<IDrawerItem> items = sideMenuItemsManager.items();
        assertThat(items).isNotNull();
        assertThat(items).isNotEmpty();
    }

    @Test
    public void getItems_shouldReturnDisabledVkModesWithNoVkAuth() {
        when(authManager.isAuthorizedOnVk()).thenReturn(true);

        Mode mode = new Mode(1, 2, Category.VK, 0, false);
        List<Mode> modes = Collections.singletonList(mode);

        List<AbstractDrawerItem> items = sideMenuItemsManager.fromModeList(modes);
        assertThat(items.get(1).isEnabled()).isFalse();
    }

    @Test
    public void isModeEnabled_shouldWorkForVkModes() {
        Mode mode = new Mode(1, 2, Category.VK, 0, false);

        when(networkManager.isOnline()).thenReturn(false);
        assertThat(sideMenuItemsManager.isModeEnabled(mode)).isFalse();

        when(networkManager.isOnline()).thenReturn(true);
        when(authManager.isAuthorizedOnVk()).thenReturn(true);
        assertThat(sideMenuItemsManager.isModeEnabled(mode)).isTrue();
        when(authManager.isAuthorizedOnVk()).thenReturn(false);
        assertThat(sideMenuItemsManager.isModeEnabled(mode)).isFalse();
    }

    @Test
    public void isModeEnabled_shouldWorkForLastfmModesWithoutAuthNeeded() {
        Mode mode = new Mode(1, 2, Category.LAST_FM, 0, false);

        when(networkManager.isOnline()).thenReturn(false);
        assertThat(sideMenuItemsManager.isModeEnabled(mode)).isFalse();

        when(networkManager.isOnline()).thenReturn(true);
        when(authManager.isAuthorizedOnLastfm()).thenReturn(true);
        when(authManager.isAuthorizedOnVk()).thenReturn(true);
        assertThat(sideMenuItemsManager.isModeEnabled(mode)).isTrue();

        when(authManager.isAuthorizedOnLastfm()).thenReturn(false);
        when(authManager.isAuthorizedOnVk()).thenReturn(true);
        assertThat(sideMenuItemsManager.isModeEnabled(mode)).isTrue();

        when(authManager.isAuthorizedOnVk()).thenReturn(false);
        assertThat(sideMenuItemsManager.isModeEnabled(mode)).isFalse();
    }

    @Test
    public void isModeEnabled_shouldWorkForOtherModes() {
        Mode mode = new Mode(1, 2, Category.OTHER, 0, false);
        assertThat(sideMenuItemsManager.isModeEnabled(mode)).isTrue();
    }

    @Test
    public void isModeEnabled_shouldWorkForLocalModes() {
        Mode mode = new Mode(1, 2, Category.LOCAL, 0, false);
        assertThat(sideMenuItemsManager.isModeEnabled(mode)).isTrue();
    }
}

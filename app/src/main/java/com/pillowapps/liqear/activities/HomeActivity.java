package com.pillowapps.liqear.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.pillowapps.liqear.BuildConfig;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.TrackedBaseActivity;
import com.pillowapps.liqear.activities.preferences.AuthActivity;
import com.pillowapps.liqear.entities.events.UpdateDrawerEvent;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.SideMenuItemsManager;
import com.pillowapps.liqear.listeners.OnModeListener;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.AppRateTheme;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;

public class HomeActivity extends TrackedBaseActivity {

    @Inject
    AuthorizationInfoManager authorizationInfoManager;
    @Inject
    SideMenuItemsManager sideMenuItemsManager;

    private Toolbar toolbar;

    private Drawer drawer;
    private OnModeListener modeListener;

    public static Intent startIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);
        LBApplication.BUS.register(this);

        if (authorizationInfoManager.isAuthScreenNeeded()) {
            Intent intent = new Intent(this, AuthActivity.class);
            intent.putExtra(Constants.SHOW_AUTHSCREEN_AUTO, true);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.main);

        AppRate.with(this)
                .theme(AppRateTheme.DARK)
                .debug(BuildConfig.DEBUG)
                .delay(1000)
                .retryPolicy(RetryPolicy.EXPONENTIAL)
                .checkAndShow();

        updateDrawer();
    }

    @Override
    protected void onDestroy() {
        LBApplication.BUS.unregister(this);
        super.onDestroy();
    }

    public void updateDrawer() {
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDrawerItems(sideMenuItemsManager.items())
                .withTranslucentStatusBar(false)
                .withSelectedItem(-1)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (modeListener != null) {
                        modeListener.onItemClick(drawerItem.getIdentifier());
                    }
                    drawer.closeDrawer();
                    return true;
                })
                .build();
    }

    @Subscribe
    public void updateDrawerEvent(UpdateDrawerEvent updateDrawerEvent) {
        updateDrawer();
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
            return;
        }
        super.onBackPressed();
    }

    public void setModeListener(OnModeListener modeListener) {
        this.modeListener = modeListener;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }
}

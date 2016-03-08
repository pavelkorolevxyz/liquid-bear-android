package com.pillowapps.liqear.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.pillowapps.liqear.BuildConfig;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.TrackedBaseActivity;
import com.pillowapps.liqear.activities.preferences.AuthActivity;
import com.pillowapps.liqear.entities.Mode;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.listeners.OnModeListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.AppRateTheme;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;

public class HomeActivity extends TrackedBaseActivity {

    @Inject
    AuthorizationInfoManager authorizationInfoManager;
    private Toolbar toolbar;

    public static Intent startIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);

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

        ArrayList<IDrawerItem> items = new ArrayList<>();
        List<Mode> modes = ModeItemsHelper.allModes;
        items.add(new SectionDrawerItem().withName("VK"));
        Mode prev;
        for (int i = 0; i < modes.size(); i++) {
            Mode current = modes.get(i);
            if (i - 1 >= 0) {
                prev = modes.get(i - 1);
                if (prev.getCategory() != current.getCategory()) {
                    items.add(new SectionDrawerItem().withName(current.getCategoryTitle()));
                }
            }
            items.add(new PrimaryDrawerItem().withName(current.getTitle()).withIcon(current.getIcon()).withIdentifier(current.getId())
                    .withIconColorRes(R.color.accent)
                    .withIconTintingEnabled(true)
                    .withSelectable(false));
        }
        OnModeListener onModeListener = new OnModeListener(this, authorizationInfoManager);
        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDrawerItems(items)
                .withSelectedItem(-1)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    onModeListener.onItemClick(drawerItem.getIdentifier());
                    return false;
                })
                .build();
    }


    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }
}

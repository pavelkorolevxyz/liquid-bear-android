package com.pillowapps.liqear.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pillowapps.liqear.BuildConfig;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.TrackedBaseActivity;
import com.pillowapps.liqear.activities.preferences.AuthActivity;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;

import javax.inject.Inject;

import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.AppRateTheme;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;

public class HomeActivity extends TrackedBaseActivity {

    @Inject
    AuthorizationInfoManager authorizationInfoManager;

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
    }
}

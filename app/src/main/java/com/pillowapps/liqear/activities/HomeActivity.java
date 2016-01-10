package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.os.Bundle;

import com.pillowapps.liqear.BuildConfig;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;

import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.AppRateTheme;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;

public class HomeActivity extends TrackedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AuthorizationInfoManager.isAuthScreenNeeded()) {
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

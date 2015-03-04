package com.pillowapps.liqear.activity;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.analytics.tracking.android.EasyTracker;
import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.audio.MusicPlaybackService;

/**
 * Preferences list activity
 */
public class PreferencesActivity extends PreferenceActivity {
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setTitle(R.string.preferences);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Preference authorizationsPref = findPreference("authorizations_preference_preferences");
        authorizationsPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent myIntent = new Intent(getApplicationContext(), AuthActivity.class);
                startActivity(myIntent);
                return true;
            }

        });
        Preference disclaimerPref = findPreference("disclaimer");
        disclaimerPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent myIntent = new Intent(getApplicationContext(),
                        TextSherlockActivity.class);
                myIntent.putExtra(TextSherlockActivity.TEXT_AIM,
                        TextSherlockActivity.Aim.DISCLAIMER);
                startActivity(myIntent);
                return true;
            }

        });
        Preference shakeNext = findPreference("shake_next");
        shakeNext.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                startService(new Intent(LiqearApplication.getAppContext(),
                        MusicPlaybackService.class)
                        .setAction(MusicPlaybackService.CHANGE_SHAKE_PREFERENCE));
                return true;
            }
        });
        Preference vkGroup = findPreference("vk_group");
        vkGroup.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                String url = "http://vk.com/liquidbear";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            }

        });
        Preference pkTwitter = findPreference("pk_twitter");
        pkTwitter.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                String url = "https://twitter.com/P_King64";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            }

        });
        Preference versionPref = findPreference("version");
        versionPref.setSummary(getVersion());
        Preference thanksPref = findPreference("thanks");
        thanksPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent myIntent = new Intent(getApplicationContext(),
                        TextSherlockActivity.class);
                myIntent.putExtra(TextSherlockActivity.TEXT_AIM, TextSherlockActivity.Aim.THANKS);
                startActivity(myIntent);
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        if (getResources().getBoolean(R.bool.analytics_enabled)) {
            EasyTracker.getInstance(this).activityStart(this);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    public String getVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return false;
    }
}
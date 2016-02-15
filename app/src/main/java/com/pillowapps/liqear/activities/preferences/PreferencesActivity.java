package com.pillowapps.liqear.activities.preferences;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.analytics.tracking.android.EasyTracker;
import com.pillowapps.liqear.BuildConfig;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.TextActivity;
import com.pillowapps.liqear.audio.MusicService;
import com.pillowapps.liqear.helpers.AppUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PreferencesActivity extends AppCompatPreferenceActivity {

    public static Intent getStartIntent(Context context) {
        return new Intent(context, PreferencesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.lb_toolbar, root, false);
        root.addView(toolbar, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.preferences);

        Preference authorizationsPref = findPreference("authorizations_preference_preferences");
        authorizationsPref.setOnPreferenceClickListener(preference -> {
            Intent myIntent = new Intent(getApplicationContext(), AuthActivity.class);
            startActivity(myIntent);
            return true;
        });
        Preference disclaimerPref = findPreference("disclaimer");
        disclaimerPref.setOnPreferenceClickListener(preference -> {
            Intent myIntent = new Intent(getApplicationContext(),
                    TextActivity.class);
            myIntent.putExtra(TextActivity.INTENTION,
                    TextActivity.Aim.DISCLAIMER);
            startActivity(myIntent);
            return true;
        });
        Preference shakeNext = findPreference("shake_next");
        shakeNext.setOnPreferenceChangeListener((preference, newValue) -> {
            startService(new Intent(PreferencesActivity.this,
                    MusicService.class)
                    .setAction(MusicService.CHANGE_SHAKE_PREFERENCE));
            return true;
        });
        Preference vkGroup = findPreference("vk_group");
        vkGroup.setOnPreferenceClickListener(preference -> {
            String url = "http://vk.com/liquidbear";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        });
        Preference pkTwitter = findPreference("pk_twitter");
        pkTwitter.setOnPreferenceClickListener(preference -> {
            String url = "https://twitter.com/P_King64";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        });
        Preference versionPref = findPreference("version");
        versionPref.setSummary(AppUtils.getAppVersion(this));
        Preference thanksPref = findPreference("thanks");
        thanksPref.setOnPreferenceClickListener(preference -> {
            Intent myIntent = new Intent(getApplicationContext(),
                    TextActivity.class);
            myIntent.putExtra(TextActivity.INTENTION, TextActivity.Aim.THANKS);
            startActivity(myIntent);
            return true;
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        if (BuildConfig.DEBUG) {
            EasyTracker.getInstance(this).activityStart(this);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
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
package com.pillowapps.liqear.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public CharSequence getToolbarTitle() {
        if (getSupportActionBar() == null) return null;
        return getSupportActionBar().getTitle();
    }

    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void toast(int messageResource) {
        Toast.makeText(this, messageResource, Toast.LENGTH_SHORT).show();
    }
}

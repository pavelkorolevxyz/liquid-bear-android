package com.pillowapps.liqear.entities;

import android.support.annotation.DrawableRes;
import android.view.View;

public class ViewPage extends Page {

    public ViewPage(View view, String title) {
        this.view = view;
        this.title = title;
    }

    public ViewPage(View view, @DrawableRes int icon) {
        this.view = view;
        this.icon = icon;
    }
}

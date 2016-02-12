package com.pillowapps.liqear.entities;

import android.view.View;

import butterknife.ButterKnife;

public class ViewPage extends Page {

    public ViewPage(View view, String title) {
        this.view = view;
        this.title = title;
        ButterKnife.bind(this, view);
    }

    public View getView() {
        return view;
    }

    public String getTitle() {
        return title;
    }

}

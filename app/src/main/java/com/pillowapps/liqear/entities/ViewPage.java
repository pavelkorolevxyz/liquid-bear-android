package com.pillowapps.liqear.entities;

import android.view.View;

import com.pillowapps.liqear.LBApplication;

import butterknife.ButterKnife;

public class ViewPage extends Page {

    public ViewPage(View view, int titleRes) {
        this.view = view;
        this.title = LBApplication.getAppContext().getString(titleRes);
        ButterKnife.bind(this, view);
    }

    public View getView() {
        return view;
    }

    public String getTitle() {
        return title;
    }
    
}

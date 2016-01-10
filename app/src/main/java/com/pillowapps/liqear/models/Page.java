package com.pillowapps.liqear.models;

import android.view.View;

public class Page {
    protected View view;
    protected String title;

    public Page(View view, String title) {
        this.view = view;
        this.title = title;
    }

    public Page() {
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

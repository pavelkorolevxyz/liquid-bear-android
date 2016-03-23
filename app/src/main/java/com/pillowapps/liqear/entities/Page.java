package com.pillowapps.liqear.entities;

import android.view.View;

public class Page {
    protected View view;
    protected String title;
    protected int icon;

    public Page(View view, String title) {
        this.view = view;
        this.title = title;
    }

    public Page(View view, int icon) {
        this.view = view;
        this.icon = icon;
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

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}

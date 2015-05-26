package com.pillowapps.liqear.components;

import android.content.Context;
import android.view.View;

import com.pillowapps.liqear.LBApplication;

import butterknife.ButterKnife;

public class ViewPage {
    private Context context;
    private View view;
    private String title;

    public ViewPage(Context context,
                    View view,
                    String title) {
        this.context = context;
        this.view = view;
        this.title = title;
    }

    public ViewPage(Context context,
                    View view,
                    int titleRes) {
        this.context = context;
        this.view = view;
        this.title = LBApplication.getAppContext().getString(titleRes);
        ButterKnife.inject(this, view);
    }

    public View getView() {
        return view;
    }

    public String getTitle() {
        return title;
    }
}

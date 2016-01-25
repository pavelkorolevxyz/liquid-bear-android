package com.pillowapps.liqear.components;

import android.content.Context;
import android.view.View;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.models.Page;

import butterknife.ButterKnife;

public class ViewPage extends Page{
    private Context context;

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
        ButterKnife.bind(this, view);
    }

    public View getView() {
        return view;
    }

    public String getTitle() {
        return title;
    }
}

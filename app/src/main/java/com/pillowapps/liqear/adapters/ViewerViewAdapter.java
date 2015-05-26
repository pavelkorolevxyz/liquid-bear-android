package com.pillowapps.liqear.adapters;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ViewerViewAdapter extends PagerAdapter {
    private List<View> views;
    private List<String> titles;

    public ViewerViewAdapter(List<View> views, List<String> titles) {
        if (views.size() != titles.size()) {
            throw new RuntimeException("Views and titles size must be equal");
        }
        this.views = views;
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public Object instantiateItem(ViewGroup pager, int position) {
        View v = views.get(position);
        pager.addView(v, 0);
        return v;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public void destroyItem(ViewGroup pager, int position, Object view) {
        pager.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void finishUpdate(ViewGroup view) {
    }

    @Override
    public void restoreState(Parcelable p, ClassLoader c) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(ViewGroup view) {
    }
}

package com.pillowapps.liqear.adapters.pagers;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.pillowapps.liqear.entities.Page;

import java.util.List;

public class PhoneFragmentPagerAdapter extends PagerAdapter {
    public static final int PLAY_TAB_INDEX = 0;
    public static final int PLAYLIST_TAB_INDEX = 1;
    private List<Page> pages;

    public PhoneFragmentPagerAdapter(List<Page> pages) {
        this.pages = pages;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pages.get(position).getTitle();
    }

    public int getImageRes(int position) {
        return pages.get(position).getIcon();
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public Object instantiateItem(ViewGroup pager, int position) {
        View v = pages.get(position).getView();
        pager.addView(v, 0);
        return v;
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

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void startUpdate(ViewGroup view) {
    }

    @Override
    public float getPageWidth(int position) {
        return super.getPageWidth(position);
    }
}

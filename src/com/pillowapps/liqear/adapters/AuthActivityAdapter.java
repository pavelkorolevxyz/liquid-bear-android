package com.pillowapps.liqear.adapters;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.R;

import java.util.List;

/**
 * Adapter of ViewPager of MainActivity.
 */
@SuppressWarnings("deprecation")
public class AuthActivityAdapter extends PagerAdapter {
    public static final int LASTFM_TAB_INDEX = 1;
    public static final int VK_TAB_INDEX = 0;

    private List<View> views = null;
    private String[] titles = new String[2];

    {
        titles[VK_TAB_INDEX] = LiqearApplication.getAppContext().getString(R.string.vk);
        titles[LASTFM_TAB_INDEX] = LiqearApplication.getAppContext().getString(R.string.last_fm);
    }

    public AuthActivityAdapter(List<View> inViews) {
        views = inViews;
    }

    public String getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object instantiateItem(View pager, int position) {
        View v = views.get(position);
        ((ViewPager) pager).addView(v, 0);
        return v;
    }

    @Override
    public void destroyItem(View pager, int position, Object view) {
        ((ViewPager) pager).removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void finishUpdate(View view) {
    }

    @Override
    public void restoreState(Parcelable p, ClassLoader c) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View view) {
    }

}

package com.pillowapps.liqear.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.global.Config;

import java.util.Arrays;
import java.util.List;

/**
 * Adapter of ViewPager of MainActivity.
 */
@SuppressWarnings("deprecation")
public class MainActivityAdapter extends PagerAdapter {
    public static final int PLAY_TAB_INDEX = 1;
    public static final int PLAYLIST_TAB_INDEX = 0;
    public static final int MODE_TAB_INDEX = 2;
    public static final int COUNT = 3;
    private static final List<String> titles = Arrays.asList(
            Config.resources.getString(R.string.playlist_tab),
            Config.resources.getString(R.string.play_tab),
            Config.resources.getString(R.string.mode_tab)
    );
    private List<View> views = null;


    public MainActivityAdapter(List<View> inViews) {
        views = inViews;
    }

    public String getTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup pager, int position) {
        View v = views.get(position);
        pager.addView(v, 0);
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

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void startUpdate(View view) {
    }


}

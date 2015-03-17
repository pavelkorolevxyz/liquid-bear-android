package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.costum.android.widget.LoadMoreListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.PagerResultSherlockActivity;
import com.pillowapps.liqear.components.ViewerPage;
import com.pillowapps.liqear.entities.Tag;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.models.lastfm.LastfmTagModel;
import com.pillowapps.liqear.network.callbacks.SimpleCallback;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class TagViewerActivity extends PagerResultSherlockActivity {
    public static final String TAG = "tag";
    public static final int TAG_INFO_INDEX = 1;
    public static final int TRACKS_INDEX = 0;
    private ViewPager pager;
    private TitlePageIndicator indicator;
    private Tag tag;
    private int page = 1;
    private LastfmTagModel tagModel = new LastfmTagModel();

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_layout);
        Bundle extras = getIntent().getExtras();
        tag = new Tag(extras.getString((TAG)));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(tag.getName());
        initUi();
        getTagTopTracks(tag, TRACKS_IN_TOP_COUNT, page++);
    }

    private void initUi() {
        initViewPager();
        for (int i = 0; i < viewersCount(); i++) {
            ViewerPage viewer = getViewer(i);
            switch (i) {
                case TRACKS_INDEX:
                    setOpenArtistListener(viewer);
                    break;
                default:
                    setOpenMainPlaylist(viewer);
                    break;
            }
        }
        getViewer(TRACKS_INDEX).getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getTagTopTracks(tag, TRACKS_IN_TOP_COUNT, page++);
            }
        });
        setTrackLongClick(getViewer(TRACKS_INDEX));
        getViewer(TRACKS_INDEX).getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                openMainPlaylist(getViewer(TRACKS_INDEX).getAdapter().getValues(), position);
            }
        });

    }

    private void initViewPager() {
        final LayoutInflater inflater = LayoutInflater.from(this);
        final List<View> views = new ArrayList<View>();
        View tab = inflater.inflate(R.layout.list_tab, null);
        views.add(tab);
        addViewer(new ViewerPage<Track>(tab));

        final TagsAdapter adapter = new TagsAdapter(views);
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapter);
        indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        indicator.setOnClickListener(null);
        indicator.setViewPager(pager);
        indicator.setTextColor(getResources().getColor(R.color.secondary_text)); indicator.setSelectedColor(getResources().getColor(R.color.primary_text));
        indicator.setFooterColor(getResources().getColor(R.color.accent));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                Intent intent = new Intent(TagViewerActivity.this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void getTagTopTracks(Tag tag, int limit, int page) {
        tagModel.getTagTopTracks(tag.getName(),
                limit, page, new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> lastfmTracks) {
                        fillTracks(Converter.convertLastfmTrackList(lastfmTracks),
                                getViewer(TRACKS_INDEX));
                        getViewer(TRACKS_INDEX).getProgressBar().setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(String error) {
                        getViewer(TRACKS_INDEX).getProgressBar().setVisibility(View.GONE);
                    }
                });
    }

    private class TagsAdapter extends PagerAdapter {

        List<View> views = null;
        private String[] titles = new String[]{
                TagViewerActivity.this.getString(R.string.tracks).toLowerCase(),
        };

        public TagsAdapter(List<View> inViews) {
            views = inViews;
        }

        public String getTitle(int position) {
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
        public CharSequence getPageTitle(int position) {
            return titles[position];
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
}
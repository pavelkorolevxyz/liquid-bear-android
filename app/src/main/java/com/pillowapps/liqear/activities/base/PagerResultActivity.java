package com.pillowapps.liqear.activities.base;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.viewers.base.ViewerPage;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.Page;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.listeners.OnViewerItemClickListener;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

public abstract class PagerResultActivity extends ResultTrackedBaseActivity {
    protected ViewPager pager;
    protected TitlePageIndicator indicator;
    private List<Page> pages = new ArrayList<>();

    public OnViewerItemClickListener<Track> trackClickListener = new OnViewerItemClickListener<Track>() {
        @Override
        public void onViewerClicked(List<Track> tracks, int position) {
            String title = String.format("%s / %s", getViewer(pager.getCurrentItem()).getTitle(), getToolbarTitle());
            openMainPlaylist(tracks, position, title);
        }
    };

    public OnViewerItemClickListener<VkTrack> vkTrackClickListener = new OnViewerItemClickListener<VkTrack>() {
        @Override
        public void onViewerClicked(List<VkTrack> tracks, int position) {
            String title = String.format("%s / %s", getViewer(pager.getCurrentItem()).getTitle(), getToolbarTitle());
            openMainPlaylist(Converter.convertVkTrackList(tracks), position, title);
        }
    };

    public OnViewerItemClickListener<Track> trackLongClickListener = (tracks, position) -> trackLongClick(tracks.get(position));
    public OnViewerItemClickListener<VkTrack> vkTrackLongClickListener = (tracks, position) -> trackLongClick(Converter.convertVkTrack(tracks.get(position)));

    public OnViewerItemClickListener<Artist> artistClickListener = (artists, position) -> openArtistByName(artists.get(position).getName());
    public OnViewerItemClickListener<Album> albumClickListener = (albums, position) -> openLastfmAlbum(albums.get(position));
    public OnViewerItemClickListener<Album> vkAlbumClickListener = (albums, position) -> openVkAlbum(albums.get(position));
    public OnViewerItemClickListener<Album> vkGroupAlbumClickListener = (albums, position) -> openGroupVkAlbum(albums.get(position));

    protected int viewersCount() {
        return pages.size();
    }

    protected ViewerPage getViewer(int i) {
        if (viewersCount() > i) {
            return (ViewerPage) pages.get(i);
        } else {
            return null;
        }
    }

    protected void addViewer(ViewerPage viewerPage) {
        pages.add(viewerPage);
    }

    protected void setPages(List<Page> pages) {
        this.pages = pages;
    }

    protected void injectViewPager(PagerAdapter adapter) {
        pager = (ViewPager) findViewById(R.id.viewpager);
        indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        pager.setAdapter(adapter);
        indicator.setOnClickListener(null);
        indicator.setViewPager(pager);
        indicator.setTextColor(ContextCompat.getColor(PagerResultActivity.this, R.color.secondary_text));
        indicator.setSelectedColor(ContextCompat.getColor(PagerResultActivity.this, R.color.primary_text));
        indicator.setFooterColor(ContextCompat.getColor(PagerResultActivity.this, R.color.accent));
    }
}

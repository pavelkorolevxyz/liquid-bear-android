package com.pillowapps.liqear.components;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.viewers.ViewerPage;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.helpers.Converter;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class PagerResultActivity extends ResultActivity {
    protected ViewPager pager;
    protected TitlePageIndicator indicator;
    private List<ViewerPage> viewers = new ArrayList<>();

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

    public OnViewerItemClickListener<Track> trackLongClickListener = new OnViewerItemClickListener<Track>() {
        @Override
        public void onViewerClicked(List<Track> tracks, int position) {
            trackLongClick(tracks.get(position));
        }
    };
    public OnViewerItemClickListener<VkTrack> vkTrackLongClickListener = new OnViewerItemClickListener<VkTrack>() {
        @Override
        public void onViewerClicked(List<VkTrack> tracks, int position) {
            trackLongClick(Converter.convertVkTrack(tracks.get(position)));
        }
    };

    public OnViewerItemClickListener<Artist> artistClickListener = new OnViewerItemClickListener<Artist>() {
        @Override
        public void onViewerClicked(List<Artist> artists, int position) {
            openArtistByName(artists.get(position).getName());
        }
    };
    public OnViewerItemClickListener<Album> albumClickListener = new OnViewerItemClickListener<Album>() {
        @Override
        public void onViewerClicked(List<Album> albums, int position) {
            openLastfmAlbum(albums.get(position));
        }
    };
    public OnViewerItemClickListener<Album> vkAlbumClickListener = new OnViewerItemClickListener<Album>() {
        @Override
        public void onViewerClicked(List<Album> albums, int position) {
            openVkAlbum(albums.get(position));
        }
    };
    public OnViewerItemClickListener<Album> vkGroupAlbumClickListener = new OnViewerItemClickListener<Album>() {
        @Override
        public void onViewerClicked(List<Album> albums, int position) {
            openGroupVkAlbum(albums.get(position));
        }
    };

    protected int viewersCount() {
        return viewers.size();
    }

    protected ViewerPage getViewer(int i) {
        if (viewersCount() > i) {
            return viewers.get(i);
        } else {
            return null;
        }
    }

    protected void addViewer(ViewerPage viewerPage) {
        viewers.add(viewerPage);
    }

    protected void setViewers(List<ViewerPage> viewers) {
        this.viewers = viewers;
    }

    protected void injectViewPager(PagerAdapter adapter) {
        pager = (ViewPager) findViewById(R.id.viewpager);
        indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        pager.setAdapter(adapter);
        indicator.setOnClickListener(null);
        indicator.setViewPager(pager);
        indicator.setTextColor(getResources().getColor(R.color.secondary_text));
        indicator.setSelectedColor(getResources().getColor(R.color.primary_text));
        indicator.setFooterColor(getResources().getColor(R.color.accent));
    }
}

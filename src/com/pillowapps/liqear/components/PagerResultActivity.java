package com.pillowapps.liqear.components;

import com.pillowapps.liqear.components.viewers.ViewerPage;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.vk.VkAlbum;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.helpers.Converter;

import java.util.ArrayList;
import java.util.List;

public class PagerResultActivity extends ResultActivity {
    private List<ViewerPage> viewers = new ArrayList<>();

    public OnViewerItemClickListener<LastfmTrack> trackClickListener = new OnViewerItemClickListener<LastfmTrack>() {
        @Override
        public void onViewerClicked(List<LastfmTrack> tracks, int position) {
            openMainPlaylist(Converter.convertLastfmTrackList(tracks), position);
        }
    };

    public OnViewerItemClickListener<VkTrack> vkTrackClickListener = new OnViewerItemClickListener<VkTrack>() {
        @Override
        public void onViewerClicked(List<VkTrack> tracks, int position) {
            openMainPlaylist(Converter.convertVkTrackList(tracks), position);
        }
    };

    public OnViewerItemClickListener<LastfmTrack> trackLongClickListener = new OnViewerItemClickListener<LastfmTrack>() {
        @Override
        public void onViewerClicked(List<LastfmTrack> tracks, int position) {
            trackLongClick(Converter.convertTrack(tracks.get(position)));
        }
    };

    public OnViewerItemClickListener<VkTrack> vkTrackLongClickListener = new OnViewerItemClickListener<VkTrack>() {
        @Override
        public void onViewerClicked(List<VkTrack> tracks, int position) {
            trackLongClick(Converter.convertVkTrack(tracks.get(position)));
        }
    };
    public OnViewerItemClickListener<LastfmArtist> artistClickListener = new OnViewerItemClickListener<LastfmArtist>() {
        @Override
        public void onViewerClicked(List<LastfmArtist> artists, int position) {
            openArtist(artists.get(position));
        }
    };

    public OnViewerItemClickListener<LastfmAlbum> albumClickListener = new OnViewerItemClickListener<LastfmAlbum>() {
        @Override
        public void onViewerClicked(List<LastfmAlbum> albums, int position) {
            openAlbum(Converter.convertAlbum(albums.get(position)));
        }
    };
    public OnViewerItemClickListener<VkAlbum> vkAlbumClickListener = new OnViewerItemClickListener<VkAlbum>() {
        @Override
        public void onViewerClicked(List<VkAlbum> albums, int position) {
            openVkAlbum(albums.get(position));
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
}

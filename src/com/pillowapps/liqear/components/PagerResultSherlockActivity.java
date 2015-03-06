package com.pillowapps.liqear.components;

import android.view.View;

import com.pillowapps.liqear.adapter.ListArrayAdapter;
import com.pillowapps.liqear.models.Album;
import com.pillowapps.liqear.models.Artist;
import com.pillowapps.liqear.models.Track;
import com.pillowapps.liqear.models.lastfm.LastfmAlbum;
import com.pillowapps.liqear.models.vk.VkAlbum;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class PagerResultSherlockActivity extends ResultSherlockActivity {
    private List<ViewerPage> viewers = new ArrayList<ViewerPage>();

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

    protected boolean adapterClean(int page) {
        return getViewer(page).getAdapter() == null;
    }

    protected void fixAdapter(ViewerPage viewer, Class clazz, List list) {
        if (viewer.adapterClean()) {
            viewer.setAdapter(new ListArrayAdapter<Track>(PagerResultSherlockActivity.this,
                    list, clazz, null));
        } else {
            viewer.getAdapter().addValues(list);
            viewer.getAdapter().notifyDataSetChanged();
            viewer.getListView().onLoadMoreComplete();
        }
        viewer.getProgressBar().setVisibility(View.GONE);
    }

    protected void fillTracks(List<Track> trackList, ViewerPage viewer) {
        Class<Track> clazz = Track.class;
        ListArrayAdapter adapter = viewer.getAdapter();
        int adapterSize = adapter == null ? 0 : adapter.getCount();
        if (adapterSize + trackList.size() == 0) {
            viewer.showEmpty();
            viewer.getProgressBar().setVisibility(View.GONE);
        } else {
            viewer.hideEmpty();
            fixAdapter(viewer, clazz, trackList);
        }
    }

    protected void fillArtists(List<Artist> artists, ViewerPage viewer) {
        Class<Artist> clazz = Artist.class;
        ListArrayAdapter adapter = viewer.getAdapter();
        int adapterSize = adapter == null ? 0 : adapter.getCount();
        if (adapterSize + artists.size() == 0) {
            viewer.showEmpty();
            viewer.getProgressBar().setVisibility(View.GONE);
        } else {
            viewer.hideEmpty();
            fixAdapter(viewer, clazz, artists);
        }
    }

    protected void fillAlbums(List<LastfmAlbum> albums, ViewerPage viewer) {
        Class<Album> clazz = Album.class;
        ListArrayAdapter adapter = viewer.getAdapter();
        int adapterSize = adapter == null ? 0 : adapter.getCount();
        if (adapterSize + albums.size() == 0) {
            viewer.showEmpty();
            viewer.getProgressBar().setVisibility(View.GONE);
        } else {
            viewer.hideEmpty();
            fixAdapter(viewer, clazz, albums);
        }
    }

    protected void fillVkAlbums(List<VkAlbum> albums, ViewerPage viewer) {
        Class<VkAlbum> clazz = VkAlbum.class;
        ListArrayAdapter adapter = viewer.getAdapter();
        int adapterSize = adapter == null ? 0 : adapter.getCount();
        if (adapterSize + albums.size() == 0) {
            viewer.showEmpty();
            viewer.getProgressBar().setVisibility(View.GONE);
        } else {
            viewer.hideEmpty();
            fixAdapter(viewer, clazz, albums);
        }
    }
}

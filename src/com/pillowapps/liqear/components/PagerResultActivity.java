package com.pillowapps.liqear.components;

import android.view.View;

import com.pillowapps.liqear.adapters.ListArrayAdapter;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.vk.VkAlbum;

import java.util.ArrayList;
import java.util.List;

public class PagerResultActivity extends ResultActivity {
    private List<ViewerPageOld> viewers = new ArrayList<ViewerPageOld>();

    protected int viewersCount() {
        return viewers.size();
    }

    protected ViewerPageOld getViewer(int i) {
        if (viewersCount() > i) {
            return viewers.get(i);
        } else {
            return null;
        }
    }

    protected void addViewer(ViewerPageOld viewerPage) {
        viewers.add(viewerPage);
    }

    protected boolean adapterClean(int page) {
        return getViewer(page).getAdapter() == null;
    }

    protected void fixAdapter(ViewerPageOld viewer, Class clazz, List list) {
        if (viewer.adapterClean()) {
            viewer.setAdapter(new ListArrayAdapter<Track>(PagerResultActivity.this,
                    list, clazz, null));
        } else {
            viewer.getAdapter().addValues(list);
            viewer.getAdapter().notifyDataSetChanged();
            viewer.getListView().onLoadMoreComplete();
        }
        viewer.getProgressBar().setVisibility(View.GONE);
    }

    protected void fillTracks(List<Track> trackList, ViewerPageOld viewer) {
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

    protected void fillArtists(List<Artist> artists, ViewerPageOld viewer) {
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

    protected void fillAlbums(List<LastfmAlbum> albums, ViewerPageOld viewer) {
        Class<LastfmAlbum> clazz = LastfmAlbum.class;
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

    protected void fillVkAlbums(List<VkAlbum> albums, ViewerPageOld viewer) {
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

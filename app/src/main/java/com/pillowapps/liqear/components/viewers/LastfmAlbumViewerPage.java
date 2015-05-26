package com.pillowapps.liqear.components.viewers;

import android.content.Context;
import android.view.View;

import com.pillowapps.liqear.adapters.LastfmAlbumAdapter;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;

import java.util.List;

public class LastfmAlbumViewerPage extends ViewerPage<LastfmAlbum> {
    private LastfmAlbumAdapter adapter;

    public LastfmAlbumViewerPage(Context context,
                                 View view,
                                 String title) {
        super(context, view, title);
    }

    public LastfmAlbumViewerPage(Context context,
                                 View view,
                                 int titleRes) {
        super(context, view, titleRes);
    }

    public LastfmAlbumAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void onItemClicked(int position) {
        onViewerItemClicked(getItems(), position);
    }

    @Override
    public boolean isNotLoaded() {
        return adapter == null;
    }

    @Override
    public List<LastfmAlbum> getItems() {
        return adapter.getItems();
    }

    public void fill(List<LastfmAlbum> albums) {
        int adapterSize = adapter == null ? 0 : adapter.getCount();

        int count = adapterSize + albums.size();
        showEmptyPlaceholder(count == 0);
        showProgressBar(false);
        updateAdapter(albums);
    }

    private void updateAdapter(List<LastfmAlbum> albums) {
        if (adapter == null) {
            adapter = new LastfmAlbumAdapter(getContext(), albums);
            listView.setAdapter(adapter);
        } else {
            adapter.addAll(albums);
            onLoadMoreComplete();
        }
    }
}

package com.pillowapps.liqear.components.viewers;

import android.content.Context;
import android.view.View;

import com.pillowapps.liqear.adapters.recyclers.AlbumAdapter;
import com.pillowapps.liqear.components.viewers.base.ViewerPage;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.helpers.Converter;

import java.util.List;

public class LastfmAlbumViewerPage extends ViewerPage<Album> {
    private AlbumAdapter adapter;
    private boolean loadImages;

    public LastfmAlbumViewerPage(Context context, View view, String title) {
        super(context, view, title);
    }

    public LastfmAlbumViewerPage(Context context, View view, int titleRes, boolean loadImages) {
        super(context, view, context.getString(titleRes));
        this.loadImages = loadImages;
    }

    public AlbumAdapter getAdapter() {
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
    public List<Album> getItems() {
        return adapter.getItems();
    }

    public void fill(List<LastfmAlbum> albums) {
        int adapterSize = adapter == null ? 0 : adapter.getItemCount();

        int count = adapterSize + albums.size();
        filledFull = (adapterSize == count);
        showEmptyPlaceholder(count == 0);
        showProgressBar(false);
        updateAdapter(Converter.convertAlbums(albums));
    }

    private void updateAdapter(List<Album> albums) {
        if (adapter == null) {
            adapter = new AlbumAdapter(albums, loadImages, listener);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.addAll(albums);
            onLoadMoreComplete();
        }
    }
}

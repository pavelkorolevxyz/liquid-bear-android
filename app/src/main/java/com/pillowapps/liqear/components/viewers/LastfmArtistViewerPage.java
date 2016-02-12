package com.pillowapps.liqear.components.viewers;

import android.content.Context;
import android.view.View;

import com.pillowapps.liqear.adapters.recyclers.ArtistAdapter;
import com.pillowapps.liqear.components.viewers.base.ViewerPage;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.helpers.Converter;

import java.util.List;

public class LastfmArtistViewerPage extends ViewerPage<Artist> {
    private ArtistAdapter adapter;
    private boolean loadImages;

    public LastfmArtistViewerPage(Context context, View view, String title, boolean loadImages) {
        super(context, view, title);
        this.loadImages = loadImages;
    }

    public LastfmArtistViewerPage(Context context, View view, int titleRes, boolean loadImages) {
        super(context, view, context.getString(titleRes));
        this.loadImages = loadImages;
    }

    public ArtistAdapter getAdapter() {
        return adapter;
    }

    public void clear() {
        setPage(1);
        adapter.clear();
    }

    @Override
    protected void onItemClicked(int position) {
        onViewerItemClicked(adapter.getItems(), position);
    }

    @Override
    public boolean isNotLoaded() {
        return adapter == null;
    }

    @Override
    public List<Artist> getItems() {
        return adapter.getItems();
    }

    public void fill(List<LastfmArtist> artists) {
        int adapterSize = adapter == null ? 0 : adapter.getItemCount();

        int count = adapterSize + artists.size();
        filledFull = (adapterSize == count);
        showEmptyPlaceholder(count == 0);
        showProgressBar(false);
        updateAdapter(Converter.convertArtistList(artists));
    }

    private void updateAdapter(List<Artist> artists) {
        if (adapter == null) {
            adapter = new ArtistAdapter(artists, loadImages, listener);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.addAll(artists);
            onLoadMoreComplete();
        }
    }
}

package com.pillowapps.liqear.components.viewers;

import android.content.Context;
import android.view.View;

import com.pillowapps.liqear.adapters.LastfmArtistAdapter;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;

import java.util.List;

public class LastfmArtistViewerPage extends ViewerPage<LastfmArtist> {
    private LastfmArtistAdapter adapter;

    public LastfmArtistViewerPage(Context context,
                                  View view,
                                  String title) {
        super(context, view, title);
    }

    public LastfmArtistViewerPage(Context context,
                                  View view,
                                  int titleRes) {
        super(context, view, titleRes);
    }

    public LastfmArtistAdapter getAdapter() {
        return adapter;
    }

    public void clear() {
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
    public List<LastfmArtist> getItems() {
        return adapter.getItems();
    }

    public void fill(List<LastfmArtist> artists) {
        int adapterSize = adapter == null ? 0 : adapter.getCount();

        int count = adapterSize + artists.size();
        boolean listsEmpty = count == 0;
        showEmptyPlaceholder(listsEmpty);
        showProgressBar(!listsEmpty);
        updateAdapter(artists);
    }

    private void updateAdapter(List<LastfmArtist> artists) {
        if (adapter == null) {
            adapter = new LastfmArtistAdapter(getContext(), artists);
        } else {
            adapter.addAll(artists);
            onLoadMoreComplete();
        }
    }
}

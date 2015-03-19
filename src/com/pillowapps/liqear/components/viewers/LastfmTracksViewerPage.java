package com.pillowapps.liqear.components.viewers;

import android.content.Context;
import android.view.View;

import com.pillowapps.liqear.adapters.LastfmTracksAdapter;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;

import java.util.List;

public class LastfmTracksViewerPage extends ViewerPage<LastfmTrack> {
    private LastfmTracksAdapter adapter;

    public LastfmTracksViewerPage(Context context,
                                  View view,
                                  String title) {
        super(context, view, title);
    }

    public LastfmTracksViewerPage(Context context,
                                  View view,
                                  int titleRes) {
        super(context, view, titleRes);
    }

    @Override
    public boolean isNotLoaded() {
        return adapter == null;
    }

    public void setAdapter(LastfmTracksAdapter adapter) {
        this.adapter = adapter;
    }


    public List<LastfmTrack> getItems() {
        return adapter.getItems();
    }

    public void clear() {
        adapter.clear();
    }

    @Override
    protected void onItemClicked(int position) {
        onViewerItemClicked(adapter.getItems(), position);
    }

    @Override
    protected boolean onItemLongClicked(int position) {
        onViewerItemLongClicked(adapter.getItems(), position);
        return true;
    }

    public void fill(List<LastfmTrack> tracks) {
        int adapterSize = adapter == null ? 0 : adapter.getCount();

        int count = adapterSize + tracks.size();
        boolean listsEmpty = count == 0;
        showEmptyPlaceholder(listsEmpty);
        showProgressBar(!listsEmpty);
        updateAdapter(tracks);
    }

    private void updateAdapter(List<LastfmTrack> tracks) {
        if (adapter == null) {
            adapter = new LastfmTracksAdapter(getContext(), tracks);
        } else {
            adapter.addAll(tracks);
            onLoadMoreComplete();
        }
    }
}

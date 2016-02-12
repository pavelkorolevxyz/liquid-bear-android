package com.pillowapps.liqear.components.viewers;

import android.content.Context;
import android.view.View;

import com.pillowapps.liqear.adapters.recyclers.TrackAdapter;
import com.pillowapps.liqear.components.viewers.base.ViewerPage;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.helpers.Converter;

import java.util.List;

public class LastfmTracksViewerPage extends ViewerPage<Track> {
    private TrackAdapter adapter;

    public LastfmTracksViewerPage(Context context,
                                  View view,
                                  String title) {
        super(context, view, title);
    }

    public LastfmTracksViewerPage(Context context,
                                  View view,
                                  int titleRes) {
        super(context, view, context.getString(titleRes));
    }

    @Override
    public boolean isNotLoaded() {
        return adapter == null || adapter.getItemCount() == 0;
    }

    public void setAdapter(TrackAdapter adapter) {
        this.adapter = adapter;
    }


    public List<Track> getItems() {
        return adapter.getItems();
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
    protected boolean onItemLongClicked(int position) {
        onViewerItemLongClicked(adapter.getItems(), position);
        return true;
    }

    public void fill(List<LastfmTrack> tracks) {
        int adapterSize = adapter == null ? 0 : adapter.getItemCount();

        int count = adapterSize + tracks.size();
        filledFull = (adapterSize == count);
        showEmptyPlaceholder(count == 0);
        showProgressBar(false);
        updateAdapter(Converter.convertLastfmTrackList(tracks));
    }

    private void updateAdapter(List<Track> tracks) {
        if (adapter == null) {
            adapter = new TrackAdapter(getContext(), tracks, listener, longClickListener);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.addAll(tracks);
            onLoadMoreComplete();
        }
    }
}

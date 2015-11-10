package com.pillowapps.liqear.components.viewers;

import android.content.Context;
import android.view.View;

import com.pillowapps.liqear.adapters.TrackAdapter;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.helpers.Converter;

import java.util.List;

public class VkTracksViewerPage extends ViewerPage<Track> {
    private TrackAdapter adapter;

    public VkTracksViewerPage(Context context,
                              View view,
                              String title) {
        super(context, view, title);
    }

    public VkTracksViewerPage(Context context,
                              View view,
                              int titleRes) {
        super(context, view, titleRes);
    }

    @Override
    public boolean isNotLoaded() {
        return adapter == null;
    }

    public void setAdapter(TrackAdapter adapter) {
        this.adapter = adapter;
    }

    public List<Track> getItems() {
        return adapter.getItems();
    }

    public void clear() {
//        adapter.clear(adapter.getItems());
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

    public void fill(List<VkTrack> tracks) {
        int adapterSize = adapter == null ? 0 : adapter.getItemCount();

        int count = adapterSize + tracks.size();
        filledFull = (adapterSize == count);
        showEmptyPlaceholder(count == 0);
        showProgressBar(false);
        updateAdapter(Converter.convertVkTrackList(tracks));
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

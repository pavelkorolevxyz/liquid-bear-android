package com.pillowapps.liqear.components.viewers;

import android.content.Context;
import android.view.View;

import com.pillowapps.liqear.adapters.VkTracksAdapter;
import com.pillowapps.liqear.entities.vk.VkTrack;

import java.util.List;

public class VkTracksViewerPage extends ViewerPage<VkTrack> {
    private VkTracksAdapter adapter;

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

    public void setAdapter(VkTracksAdapter adapter) {
        this.adapter = adapter;
    }


    public List<VkTrack> getItems() {
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

    public void fill(List<VkTrack> tracks) {
        int adapterSize = adapter == null ? 0 : adapter.getCount();

        int count = adapterSize + tracks.size();
        boolean listsEmpty = count == 0;
        showEmptyPlaceholder(listsEmpty);
        showProgressBar(!listsEmpty);
        updateAdapter(tracks);
    }

    private void updateAdapter(List<VkTrack> tracks) {
        if (adapter == null) {
            adapter = new VkTracksAdapter(getContext(), tracks);
        } else {
            adapter.addAll(tracks);
            onLoadMoreComplete();
        }
    }
}

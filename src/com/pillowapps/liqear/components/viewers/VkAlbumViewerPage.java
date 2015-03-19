package com.pillowapps.liqear.components.viewers;

import android.content.Context;
import android.view.View;

import com.pillowapps.liqear.adapters.VkAlbumAdapter;
import com.pillowapps.liqear.entities.vk.VkAlbum;

import java.util.List;

public class VkAlbumViewerPage extends ViewerPage<VkAlbum> {
    private VkAlbumAdapter adapter;

    public VkAlbumViewerPage(Context context,
                             View view,
                             String title) {
        super(context, view, title);
    }

    public VkAlbumViewerPage(Context context,
                             View view,
                             int titleRes) {
        super(context, view, titleRes);
    }

    @Override
    public boolean isNotLoaded() {
        return adapter == null;
    }

    public void setAdapter(VkAlbumAdapter adapter) {
        this.adapter = adapter;
    }


    public List<VkAlbum> getItems() {
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

    public void fill(List<VkAlbum> albums) {
        int adapterSize = adapter == null ? 0 : adapter.getCount();

        int count = adapterSize + albums.size();
        boolean listsEmpty = count == 0;
        showEmptyPlaceholder(listsEmpty);
        showProgressBar(!listsEmpty);
        updateAdapter(albums);
    }

    private void updateAdapter(List<VkAlbum> albums) {
        if (adapter == null) {
            adapter = new VkAlbumAdapter(getContext(), albums);
        } else {
            adapter.addAll(albums);
            onLoadMoreComplete();
        }
    }
}

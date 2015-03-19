package com.pillowapps.liqear.components.viewers;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.costum.android.widget.LoadMoreListView;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.OnViewerItemClickListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class ViewerPage<T> {
    @InjectView(R.id.list)
    protected LoadMoreListView listView;
    @InjectView(R.id.pageProgressBar)
    protected ProgressBar progressBar;
    @InjectView(R.id.empty)
    protected TextView emptyTextView;
    private Context context;

    private int page = 1;
    private int totalPages = Integer.MAX_VALUE;
    private View view;
    private String title;

    private final AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            onItemClicked(position);
        }
    };
    private final AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
            return onItemLongClicked(position);
        }
    };

    private OnViewerItemClickListener<T> itemClickListener;
    private OnViewerItemClickListener<T> itemLongClickListener;

    public ViewerPage(Context context,
                      View view,
                      String title) {
        this.context = context;
        this.view = view;
        this.title = title;
        ButterKnife.inject(this, view);
        listView.setOnItemClickListener(listener);
        listView.setOnItemLongClickListener(longClickListener);
    }

    public ViewerPage(Context context,
                      View view,
                      int titleRes) {
        this.context = context;
        this.view = view;
        this.title = LBApplication.getAppContext().getString(titleRes);
        ButterKnife.inject(this, view);
        listView.setOnItemClickListener(listener);
        listView.setOnItemLongClickListener(longClickListener);
    }

    public void onLoadMoreComplete() {
        listView.onLoadMoreComplete();
    }

    public int getPage() {
        if (totalPages < page++) {
            listView.onLoadMoreComplete();
            return -1;
        }
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Context getContext() {
        return context;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void showProgressBar(boolean show) {
        if (progressBar == null) return;
        int wantedVisibility = show ? View.VISIBLE : View.GONE;
        if (wantedVisibility != progressBar.getVisibility()) {
            progressBar.setVisibility(wantedVisibility);
        }
    }

    public void showEmptyPlaceholder(boolean show) {
        if (emptyTextView == null) return;
        int wantedVisibility = show ? View.VISIBLE : View.GONE;
        if (wantedVisibility != emptyTextView.getVisibility()) {
            emptyTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public View getView() {
        return view;
    }

    public String getTitle() {
        return title;
    }

    public void setOnLoadMoreListener(LoadMoreListView.OnLoadMoreListener listener) {
        listView.setOnLoadMoreListener(listener);
    }

    public void setItemClickListener(OnViewerItemClickListener<T> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(OnViewerItemClickListener<T> itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    protected void onViewerItemClicked(List<T> items, int position) {
        itemClickListener.onViewerClicked(items, position);
    }

    protected void onViewerItemLongClicked(List<T> items, int position) {
        itemLongClickListener.onViewerClicked(items, position);
    }

    public void onLoadMore() {
        listView.onLoadMore();
    }

    protected abstract void onItemClicked(int position);

    protected boolean onItemLongClicked(int position) {
        return false;
    }

    public abstract boolean isNotLoaded();

    public abstract List<T> getItems();
}

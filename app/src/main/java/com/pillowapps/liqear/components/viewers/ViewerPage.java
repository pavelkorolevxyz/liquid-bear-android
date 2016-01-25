package com.pillowapps.liqear.components.viewers;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.EndlessRecyclerOnScrollListener;
import com.pillowapps.liqear.components.OnLoadMoreListener;
import com.pillowapps.liqear.components.OnRecyclerItemClickListener;
import com.pillowapps.liqear.components.OnRecyclerLongItemClickListener;
import com.pillowapps.liqear.components.OnViewerItemClickListener;
import com.pillowapps.liqear.helpers.DividerItemDecoration;
import com.pillowapps.liqear.models.Page;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public abstract class ViewerPage<T> extends Page {
    @Bind(R.id.list)
    protected RecyclerView recyclerView;
    @Bind(R.id.pageProgressBar)
    protected ProgressBar progressBar;
    @Bind(R.id.empty)
    protected TextView emptyTextView;
    protected boolean filledFull = false;

    private Context context;
    private boolean singlePage = false;
    private int page = 1;

    public final OnRecyclerItemClickListener listener = (view1, position) -> ViewerPage.this.onItemClicked(position);
    public final OnRecyclerLongItemClickListener longClickListener = (view1, position) -> ViewerPage.this.onItemLongClicked(position);

    private OnViewerItemClickListener<T> itemClickListener;
    private OnViewerItemClickListener<T> itemLongClickListener;
    private OnLoadMoreListener<T> onLoadMoreListener;

    public ViewerPage(Context context, View view, String title) {
        this.context = context;
        this.view = view;
        this.title = title;
        ButterKnife.bind(this, view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                Timber.d("onLoadMore " + ViewerPage.this.title + " " + onLoadMoreListener);
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.onLoadMore();
                }
            }
        });
    }

    public ViewerPage(Context context, View view, int titleRes) {
        this(context, view, LBApplication.getAppContext().getString(titleRes));
    }

    public void onLoadMoreComplete() {
//        recycler.onLoadMoreComplete();
    }

    public int getPage() {
        Timber.d(page + " - PAGE of viewer " + title);
        return page++;
    }

    public int getVkPage() {
        int vkPage = page - 1;
        page++;
        return vkPage;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Context getContext() {
        return context;
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

    public void setSinglePage(boolean singlePage) {
        this.singlePage = singlePage;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener<T> listener) {
        this.onLoadMoreListener = listener;
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
        onLoadMoreListener.onLoadMore();
    }

    protected abstract void onItemClicked(int position);

    protected boolean onItemLongClicked(int position) {
        return true;
    }

    public abstract boolean isNotLoaded();

    public abstract List<T> getItems();
}

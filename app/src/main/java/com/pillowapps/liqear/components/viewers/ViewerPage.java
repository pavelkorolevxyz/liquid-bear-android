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

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public abstract class ViewerPage<T> {
    private final LinearLayoutManager layoutManager;
    @InjectView(R.id.list)
    protected RecyclerView recyclerView;
    @InjectView(R.id.pageProgressBar)
    protected ProgressBar progressBar;
    @InjectView(R.id.empty)
    protected TextView emptyTextView;
    protected boolean filledFull = false;

    private Context context;
    private boolean singlePage = false;
    private int page = 1;
    private View view;
    private String title;

    public final OnRecyclerItemClickListener listener = new OnRecyclerItemClickListener() {
        @Override
        public void onItemClicked(View view, int position) {
            ViewerPage.this.onItemClicked(position);
        }
    };
    public final OnRecyclerLongItemClickListener longClickListener = new OnRecyclerLongItemClickListener() {
        @Override
        public boolean onItemLongClicked(View view, int position) {
            return ViewerPage.this.onItemLongClicked(position);
        }
    };

    private OnViewerItemClickListener<T> itemClickListener;
    private OnViewerItemClickListener<T> itemLongClickListener;
    private OnLoadMoreListener<T> onLoadMoreListener;

    public ViewerPage(Context context, View view, String title) {
        this.context = context;
        this.view = view;
        this.title = title;
        ButterKnife.inject(this, view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

//        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page) {
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

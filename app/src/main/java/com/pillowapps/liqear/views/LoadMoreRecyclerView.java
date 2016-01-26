package com.pillowapps.liqear.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.pillowapps.liqear.listeners.OnRecyclerScrolledToBottomListener;
import com.pillowapps.liqear.listeners.OnLoadMoreListener;

public class LoadMoreRecyclerView extends RecyclerView {

    private OnLoadMoreListener onLoadMoreListener;

    public LoadMoreRecyclerView(Context context) {
        super(context);
        init();
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        initWithLayoutManager();
    }

    private void initWithLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(layoutManager);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void enableLoadMore(boolean enable) {
        if (enable) {
            initLoadMore();
        } else {
            clearOnScrollListeners();
        }
    }

    private void initLoadMore() {
        addOnScrollListener(new OnRecyclerScrolledToBottomListener((LinearLayoutManager) getLayoutManager()) {
            @Override
            public void onLoadMore(int currentPage) {
                onLoadMoreListener.onLoadMore();
            }
        });
    }


}

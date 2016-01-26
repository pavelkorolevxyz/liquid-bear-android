package com.pillowapps.liqear.listeners;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class OnRecyclerScrolledToBottomListener extends RecyclerView.OnScrollListener {
    private int previousTotal = 0;
    private boolean loading = true;
    private static final int VISIBLE_THRESHOLD = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int currentPage = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public OnRecyclerScrolledToBottomListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
            // End has been reached

            // Do something
            currentPage++;

            onLoadMore(currentPage);

            loading = true;
        }
    }

    public abstract void onLoadMore(int currentPage);
}

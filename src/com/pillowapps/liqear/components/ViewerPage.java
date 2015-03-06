package com.pillowapps.liqear.components;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.costum.android.widget.LoadMoreListView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.adapter.ListArrayAdapter;

import java.util.List;

public class ViewerPage<T> {
    private View tab;
    private ListArrayAdapter<T> adapter;
    private int page;
    private LoadMoreListView listView;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private String title;
    private int totalPages = Integer.MAX_VALUE;

    public ViewerPage(View tab) {
        this.tab = tab;
        clear();
        listView = (LoadMoreListView) tab.findViewById(R.id.list);
        progressBar = (ProgressBar) tab.findViewById(R.id.pageProgressBar);
        emptyTextView = (TextView) tab.findViewById(R.id.empty);
    }

    public void clear() {
        page = 1;
        if (adapter != null) {
            adapter.clear();
            listView.setAdapter(adapter);
        }
    }

    public View getTab() {
        return tab;
    }

    public void setTab(View tab) {
        this.tab = tab;
    }

    public ListArrayAdapter<T> getAdapter() {
        return adapter;
    }

    public void setAdapter(ListArrayAdapter<T> adapter) {
        this.adapter = adapter;
        listView.setAdapter(adapter);
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

    public int getPage(String temp) {
        if (totalPages < page + 1) {
            listView.onLoadMoreComplete();
            return -1;
        }
        return page++;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public LoadMoreListView getListView() {
        return listView;
    }

    public void setListView(LoadMoreListView listView) {
        this.listView = listView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void showEmpty() {
        emptyTextView.setVisibility(View.VISIBLE);
    }

    public void hideEmpty() {
        emptyTextView.setVisibility(View.GONE);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public T get(int position) {
        return adapter.get(position);
    }

    public List<T> getValues() {
        return adapter.getValues();
    }

    public boolean adapterClean() {
        return adapter == null;
    }
}

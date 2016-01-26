package com.pillowapps.liqear.listeners;

import java.util.List;

public interface OnViewerItemClickListener<T> {
    void onViewerClicked(List<T> items, int position);
}

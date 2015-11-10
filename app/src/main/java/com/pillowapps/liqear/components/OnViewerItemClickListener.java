package com.pillowapps.liqear.components;

import java.util.List;

public interface OnViewerItemClickListener<T> {
    void onViewerClicked(List<T> items, int position);
}

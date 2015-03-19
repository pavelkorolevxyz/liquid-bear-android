package com.pillowapps.liqear.components;

import java.util.List;

public interface OnViewerItemClickListener<T> {
    public void onViewerClicked(List<T> items, int position);
}

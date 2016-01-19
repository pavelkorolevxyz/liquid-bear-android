package com.pillowapps.liqear.entities;

import android.view.LayoutInflater;
import android.view.View;

public interface Item {
    int getViewType();
    View getView(LayoutInflater inflater, View convertView);
}
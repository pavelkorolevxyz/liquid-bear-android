package com.pillowapps.liqear.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.pillowapps.liqear.components.UpdateAdapterCallback;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.entities.Item;
import com.pillowapps.liqear.entities.Mode;

import java.util.ArrayList;
import java.util.List;

public class ModeListAdapter extends ArrayAdapter<Item> {
    private List<DataSetObserver> observers = new ArrayList<>();
    private LayoutInflater mInflater;
    private ModeItemsHelper helper;

    public ModeListAdapter(Context context, List<Item> items, ModeItemsHelper helper) {
        super(context, 0, items);
        mInflater = LayoutInflater.from(context);
        this.helper = helper;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        observers.add(observer);
    }

    public void notifyDataSetChanged() {
        for (DataSetObserver observer : observers) {
            observer.onChanged();
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        observers.remove(dataSetObserver);
    }

    @Override
    public int getCount() {
        return helper.getItems().size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public List<Mode> getValues() {
        return helper.getModes();
    }

    public void notifyChanges() {
        clear();
        for (Item item : helper.createItemsList(new UpdateAdapterCallback() {
            @Override
            public void onUpdate() {
                notifyChanges();
            }
        })) {
            add(item);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;

    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(mInflater, convertView);
    }

    public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }
}

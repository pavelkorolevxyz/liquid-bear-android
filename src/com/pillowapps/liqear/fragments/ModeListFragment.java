package com.pillowapps.liqear.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activity.MainActivity;
import com.pillowapps.liqear.components.ModeClickListener;
import com.pillowapps.liqear.components.ModeListAdapter;
import com.pillowapps.liqear.components.UpdateAdapterCallback;
import com.pillowapps.liqear.helpers.ModeItemsHelper;

public class ModeListFragment extends ListFragment {
    private ModeListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UpdateAdapterCallback callback = new UpdateAdapterCallback() {
            @Override
            public void onUpdate() {
                adapter.notifyChanges();
            }
        };
        ModeItemsHelper modeItemsHelper = new ModeItemsHelper();
        adapter = new ModeListAdapter(getActivity(), modeItemsHelper.createItemsList(callback), modeItemsHelper);
        setListAdapter(adapter);
        getListView().setOnItemLongClickListener(new ModeLongClickListener());
        getListView().setOnItemClickListener(new ModeClickListener((MainActivity) getActivity()));
    }

    public ModeListAdapter getAdapter() {
        return adapter;
    }

    public class ModeLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            ModeItemsHelper.setEditMode(true);
            adapter.notifyChanges();
            return true;
        }
    }
}

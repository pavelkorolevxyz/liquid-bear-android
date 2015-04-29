package com.pillowapps.liqear.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.components.ModeClickListener;
import com.pillowapps.liqear.adapters.ModeListAdapter;
import com.pillowapps.liqear.components.UpdateAdapterCallback;
import com.pillowapps.liqear.helpers.ModeItemsHelper;

public class ModeListFragment extends ListFragment {
    private ModeListAdapter adapter;
    private NavigationDrawerCallbacks mCallbacks;
    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, null);
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
        getListView().setBackgroundColor(getResources().getColor(R.color.icons));
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


    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        FragmentActivity activity = getActivity();
        mFragmentContainerView = activity.findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void selectItem(int position) {
        if (getListView() != null) {
            getListView().setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    public static interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int position);
    }
}

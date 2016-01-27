package com.pillowapps.liqear.fragments.base;

import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseFragment extends Fragment {

    protected void toast(int stringResId) {
        Toast.makeText(getActivity(), stringResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

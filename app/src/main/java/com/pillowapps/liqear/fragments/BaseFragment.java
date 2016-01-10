package com.pillowapps.liqear.fragments;

import android.support.v4.app.Fragment;
import android.widget.Toast;

public class BaseFragment extends Fragment {

    protected void toast(int stringResId) {
        Toast.makeText(getActivity(), stringResId, Toast.LENGTH_SHORT).show();
    }

}

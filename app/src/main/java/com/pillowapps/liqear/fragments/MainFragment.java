package com.pillowapps.liqear.fragments;

import android.support.v4.app.Fragment;

import com.pillowapps.liqear.activities.MainActivity;

public class MainFragment extends Fragment {

    public MainActivity mainActivity;

    public void openDropButton() {
        mainActivity.openDropButton();
    }

    public void toggleLoveCurrentTrack(){
        mainActivity.toggleLoveCurrentTrack();
    }
}

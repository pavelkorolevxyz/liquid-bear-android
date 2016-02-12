package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.view.View;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.ListBaseActivity;
import com.pillowapps.liqear.adapters.recyclers.NeighbourAdapter;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.entities.lastfm.LastfmUser;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.LBPreferencesManager;
import com.pillowapps.liqear.models.lastfm.LastfmUserModel;

import java.util.List;

import javax.inject.Inject;

public class LastfmNeighboursActivity extends ListBaseActivity {

    private NeighbourAdapter adapter;

    @Inject
    LastfmUserModel lastfmUserModel;
    @Inject
    AuthorizationInfoManager authorizationInfoManager;
    @Inject
    LBPreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LBApplication.get(this).applicationComponent().inject(this);

        setTitle(getResources().getString(R.string.neighbours));
        if (authorizationInfoManager.isAuthorizedOnLastfm()) {
            getNeighbours(authorizationInfoManager.getLastfmName(), getPageSize());
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void fillWithUsers(List<User> users) {
        adapter = new NeighbourAdapter(users, preferencesManager.isDownloadImagesEnabled(), (view, position) -> openLastfmUser(adapter.getItem(position)));
        recycler.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        updateEmptyTextView();
    }

    private void getNeighbours(String username, int limit) {
        lastfmUserModel.getNeighbours(username, limit,
                new SimpleCallback<List<LastfmUser>>() {
                    @Override
                    public void success(List<LastfmUser> lastfmUsers) {
                        fillWithUsers(Converter.convertUsers(lastfmUsers));
                    }

                    @Override
                    public void failure(String error) {
                        showError(error);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}

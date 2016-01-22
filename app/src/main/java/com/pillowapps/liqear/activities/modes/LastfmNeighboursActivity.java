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
import com.pillowapps.liqear.models.lastfm.LastfmUserModel;

import java.util.List;

import javax.inject.Inject;

public class LastfmNeighboursActivity extends ListBaseActivity {

    private NeighbourAdapter adapter;

    @Inject
    LastfmUserModel lastfmUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LBApplication.get(this).applicationComponent().inject(this);

        actionBar.setTitle(getResources().getString(R.string.neighbours));
        if (AuthorizationInfoManager.isAuthorizedOnLastfm()) {
            getNeighbours(AuthorizationInfoManager.getLastfmName(), getPageSize());
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void fillWithUsers(List<User> users) {
        emptyTextView.setVisibility(users.size() == 0 ? View.VISIBLE : View.GONE);
        adapter = new NeighbourAdapter(users, (view, position) -> openLastfmUser(adapter.getItem(position)));
        recycler.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
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

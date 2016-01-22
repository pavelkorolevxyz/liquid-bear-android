package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.adapters.recyclers.UserAdapter;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.entities.lastfm.LastfmUser;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.DelayedTextWatcher;
import com.pillowapps.liqear.models.lastfm.LastfmUserModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class SearchLastfmUserActivity extends SearchBaseActivity {

    private UserAdapter adapter;

    @Inject
    LastfmUserModel lastfmUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);

        actionBar.setTitle(getResources().getString(R.string.friends));
        if (AuthorizationInfoManager.isAuthorizedOnLastfm()) {
            getLastfmFriends(AuthorizationInfoManager.getLastfmName(), getPageSize(), 1);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initWatcher() {
        editText.addTextChangedListener(new DelayedTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                runTaskWithDelay(() -> {
                    String searchQuery = editText.getText().toString().trim();
                    if (searchQuery.length() == 0) {
                        return;
                    }
                    getUserInfo(searchQuery);
                });
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void fillWithUsers(List<User> users) {
        emptyTextView.setVisibility(users.size() == 0 ? View.VISIBLE : View.GONE);
        adapter = new UserAdapter(users, (view, position) -> openLastfmUser(adapter.getItem(position)));
        recycler.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    private void getUserInfo(String name) {
        lastfmUserModel.getUserInfo(name, new SimpleCallback<LastfmUser>() {
                    @Override
                    public void success(LastfmUser user) {
                        List<User> users = Collections.singletonList(Converter.convertUser(user));
                        fillWithUsers(users);
                    }

                    @Override
                    public void failure(String error) {
                        showError(error);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void getLastfmFriends(String username, int limit, int page) {
        lastfmUserModel.getLastfmFriends(username, limit, page, new SimpleCallback<List<LastfmUser>>() {
                    @Override
                    public void success(List<LastfmUser> lastfmUsers) {
                        fillWithUsers(Converter.convertUsers(lastfmUsers));
                    }

                    @Override
                    public void failure(String error) {
                        showError(error);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }
}

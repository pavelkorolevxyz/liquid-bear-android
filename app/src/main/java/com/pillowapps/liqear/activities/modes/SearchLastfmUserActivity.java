package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.adapters.UserAdapter;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.entities.lastfm.LastfmUser;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.DelayedTextWatcher;
import com.pillowapps.liqear.models.lastfm.LastfmUserModel;

import java.util.Arrays;
import java.util.List;

public class SearchLastfmUserActivity extends SearchBaseActivity {

    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                runTaskWithDelay(new Runnable() {
                    @Override
                    public void run() {
                        String searchQuery = editText.getText().toString().trim();
                        if (searchQuery.length() == 0) {
                            return;
                        }
                        getUserInfo(searchQuery);
                    }
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
        adapter = new UserAdapter(users, new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                openLastfmUser(adapter.getItem(position));
            }
        });
        recycler.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    private void getUserInfo(String name) {
        new LastfmUserModel().getUserInfo(name, new SimpleCallback<LastfmUser>() {
                    @Override
                    public void success(LastfmUser user) {
                        List<User> users = Arrays.asList(Converter.convertUser(user));
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
        new LastfmUserModel().getLastfmFriends(username, limit, page, new SimpleCallback<List<LastfmUser>>() {
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

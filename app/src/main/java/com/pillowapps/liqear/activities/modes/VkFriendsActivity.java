package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.view.View;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.ListBaseActivity;
import com.pillowapps.liqear.adapters.recyclers.UserAdapter;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkUser;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.PreferencesScreenManager;
import com.pillowapps.liqear.models.vk.VkFriendModel;

import java.util.List;

import javax.inject.Inject;

public class VkFriendsActivity extends ListBaseActivity {

    private UserAdapter adapter;

    @Inject
    VkFriendModel vkFriendModel;
    @Inject
    PreferencesScreenManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);

        setTitle(getResources().getString(R.string.vk_friends));
        getVkFriends();
    }

    private void fillWithUsers(List<User> users) {
        adapter = new UserAdapter(users, preferencesManager.isDownloadImagesEnabled(), (view, position) -> openVkUser(adapter.getItem(position)));
        recycler.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        updateEmptyTextView();
    }

    private void getVkFriends() {
        vkFriendModel.getFriends(0, 0, new VkSimpleCallback<List<VkUser>>() {
            @Override
            public void success(List<VkUser> data) {
                fillWithUsers(Converter.convertVkUserList(data));
            }

            @Override
            public void failure(VkError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}

package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.view.View;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.adapters.UserAdapter;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkUser;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.models.vk.VkFriendModel;

import java.util.List;

public class VkFriendsActivity extends ListBaseActivity {

    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setTitle(getResources().getString(R.string.vk_friends));
        getVkFriends();
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

    private void getVkFriends() {
        new VkFriendModel().getFriends(0, 0, new VkSimpleCallback<List<VkUser>>() {
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

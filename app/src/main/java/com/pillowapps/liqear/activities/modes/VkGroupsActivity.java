package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.view.View;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.ListBaseActivity;
import com.pillowapps.liqear.adapters.recyclers.GroupAdapter;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.Group;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkGroup;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.models.vk.VkGroupModel;

import java.util.List;

public class VkGroupsActivity extends ListBaseActivity {

    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setTitle(getResources().getString(R.string.group));
        getVkGroups();
    }

    private void fillWithGroups(List<Group> groups) {
        emptyTextView.setVisibility(groups.size() == 0 ? View.VISIBLE : View.GONE);
        adapter = new GroupAdapter(groups, (view, position) -> openGroup(adapter.getItem(position)));
        recycler.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    private void getVkGroups() {
        new VkGroupModel().getGroups(0, 0, new VkSimpleCallback<List<VkGroup>>() {
            @Override
            public void success(List<VkGroup> data) {
                List<Group> groups = Converter.convertGroups(data);
                fillWithGroups(groups);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(VkError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}

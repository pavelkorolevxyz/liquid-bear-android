package com.pillowapps.liqear.activities.modes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.ListBaseActivity;
import com.pillowapps.liqear.adapters.recyclers.GroupAdapter;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.Group;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkGroup;
import com.pillowapps.liqear.helpers.PreferencesScreenManager;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.models.vk.VkGroupModel;

import java.util.List;

import javax.inject.Inject;

public class VkGroupsActivity extends ListBaseActivity {

    private GroupAdapter adapter;

    @Inject
    VkGroupModel vkGroupModel;
    @Inject
    PreferencesScreenManager preferencesManager;

    public static Intent startIntent(Context context) {
        return new Intent(context, VkGroupsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);

        setTitle(getResources().getString(R.string.group));
        getVkGroups();
    }

    private void fillWithGroups(List<Group> groups) {
        adapter = new GroupAdapter(groups, preferencesManager.isDownloadImagesEnabled(), (view, position) -> openGroup(adapter.getItem(position)));
        recycler.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        updateEmptyTextView();
    }

    private void getVkGroups() {
        vkGroupModel.getGroups(0, 0, new VkSimpleCallback<List<VkGroup>>() {
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

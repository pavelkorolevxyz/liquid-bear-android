package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.view.View;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.adapters.TrackAdapter;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.TrackUtils;
import com.pillowapps.liqear.models.vk.VkAudioModel;

import java.util.List;

public class VkAudioSearchActivity extends ListBaseActivity {

    private TrackAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setTitle(getResources().getString(R.string.pick_good_result));
        Track currentTrack = Timeline.getInstance().getCurrentTrack();

        String target = getIntent().getStringExtra(Constants.TARGET);
        if (target != null && !target.isEmpty()) {
            searchVK(target, 100);
        } else {
            searchVK(TrackUtils.getNotation(currentTrack), 100);
        }
    }

    private void fillWithVkTracklist(List<VkTrack> vkTracks) {
        final List<Track> trackList = Converter.convertVkTrackList(vkTracks);
        if (adapter == null || adapter.getItemCount() == 0) {
            emptyTextView.setVisibility(trackList.size() == 0 ? View.VISIBLE : View.GONE);
            adapter = new TrackAdapter(trackList, new OnRecyclerItemClickListener() {
                @Override
                public void onItemClicked(View view, int position) {
                    openMainPlaylist(adapter.getItems(), position, getToolbarTitle());
                }
            }, new OnRecyclerLongItemClickListener() {
                @Override
                public void onItemLongClicked(View view, int position) {
                    trackLongClick(adapter.getItems(), position);
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.addItems(trackList);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void searchVK(String searchQuery, int count) {
        new VkAudioModel().searchAudio(searchQuery, 0, count, new VkSimpleCallback<List<VkTrack>>() {
            @Override
            public void success(List<VkTrack> data) {
                fillWithVkTracklist(data);
//                adapter.setHighlighted(SharedPreferencesManager.getUrlNumberPreferences()
//                        .getInt(getIntent().getStringExtra(Constants.TARGET), 0)); todo
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(VkError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}

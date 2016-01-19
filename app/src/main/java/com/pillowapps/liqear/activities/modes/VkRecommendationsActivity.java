package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.ListBaseActivity;
import com.pillowapps.liqear.adapters.recyclers.TrackAdapter;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.models.vk.VkAudioModel;

import java.util.List;

public class VkRecommendationsActivity extends ListBaseActivity {

    private TrackAdapter adapter;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setTitle(getResources().getString(R.string.recommendations));
        searchVkRecommendations(getPageSize(), page++);
        recycler.enableLoadMore(true);
        recycler.setOnLoadMoreListener(() -> searchVkRecommendations(getPageSize(), page++));
    }

    private void fillWithVkTracklist(List<VkTrack> vkTracks) {
        final List<Track> trackList = Converter.convertVkTrackList(vkTracks);
        if (adapter == null || adapter.getItemCount() == 0) {
            emptyTextView.setVisibility(trackList.size() == 0 ? View.VISIBLE : View.GONE);
            adapter = new TrackAdapter(this, trackList,
                    (view, position) -> openMainPlaylist(adapter.getItems(), position, getToolbarTitle()),
                    (view, position) -> {
                        trackLongClick(adapter.getItems(), position);
                        return true;
                    });
            recycler.setAdapter(adapter);
        } else {
            adapter.addAll(trackList);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void searchVkRecommendations(int limit, int page) {
        new VkAudioModel().getVkRecommendations(limit, page * limit,
                new VkSimpleCallback<List<VkTrack>>() {
                    @Override
                    public void success(List<VkTrack> data) {
                        fillWithVkTracklist(data);
                    }

                    @Override
                    public void failure(VkError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.to_playlist_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.to_playlist: {
                if (adapter == null) return true;
                addToMainPlaylist(adapter.getItems());
                Toast.makeText(VkRecommendationsActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                if (adapter == null) return true;
                saveAsPlaylist(adapter.getItems());
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

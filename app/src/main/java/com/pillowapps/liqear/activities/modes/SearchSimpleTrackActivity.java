package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.adapters.TrackAdapter;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.DelayedTextWatcher;
import com.pillowapps.liqear.models.vk.VkAudioModel;

import java.util.List;

public class SearchSimpleTrackActivity extends SearchBaseActivity {

    private TrackAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setTitle(getString(R.string.vk_simple_search));
        editText.setFloatingLabelText(getString(R.string.track));
        editText.setHint(getString(R.string.track));
        progressBar.setVisibility(View.GONE);

        initWatcher();
    }

    protected void initWatcher() {
        editText.addTextChangedListener(new DelayedTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                runTaskWithDelay(new Runnable() {
                    @Override
                    public void run() {
                        String searchQuery = editText.getText().toString().trim();
                        if (searchQuery.length() == 0) return;
                        searchVK(searchQuery, getPageSize());
                    }
                });
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void fillWithVkTracklist(List<VkTrack> vkTracks) {
        List<Track> trackList = Converter.convertVkTrackList(vkTracks);
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
        progressBar.setVisibility(View.GONE);
    }

    private void searchVK(String searchQuery, int count) {
        new VkAudioModel().searchAudio(searchQuery, 0, count, new VkSimpleCallback<List<VkTrack>>() {
            @Override
            public void success(List<VkTrack> data) {
                fillWithVkTracklist(data);
            }

            @Override
            public void failure(VkError error) {
                showError(error);
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
                Toast.makeText(SearchSimpleTrackActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
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

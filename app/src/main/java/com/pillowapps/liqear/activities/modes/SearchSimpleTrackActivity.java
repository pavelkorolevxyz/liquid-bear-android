package com.pillowapps.liqear.activities.modes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.SearchListBaseActivity;
import com.pillowapps.liqear.adapters.recyclers.TrackAdapter;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.DelayedTextWatcher;
import com.pillowapps.liqear.models.vk.VkAudioModel;

import java.util.List;

import javax.inject.Inject;

public class SearchSimpleTrackActivity extends SearchListBaseActivity {

    private TrackAdapter adapter;

    @Inject
    VkAudioModel vkAudioModel;

    public static Intent startIntent(Context context) {
        return new Intent(context, SearchSimpleTrackActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);

        setTitle(getString(R.string.vk_simple_search));
        editText.setFloatingLabelText(getString(R.string.track));
        editText.setHint(getString(R.string.track));
        progressBar.setVisibility(View.GONE);

        initWatcher();
    }

    protected void initWatcher() {
        editText.addTextChangedListener(new DelayedTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                runTaskWithDelay(() -> {
                    String searchQuery = editText.getText().toString().trim();
                    if (searchQuery.length() == 0) return;
                    searchVK(searchQuery, getPageSize());
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
        adapter = new TrackAdapter(this, trackList,
                (view, position) -> openMainPlaylist(adapter.getItems(), position, getToolbarTitle()),
                (view, position) -> {
                    trackLongClick(adapter.getItems(), position);
                    return true;
                });
        recycler.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        updateEmptyTextView();
    }

    private void searchVK(String searchQuery, int count) {
        vkAudioModel.searchAudio(searchQuery, 0, count, new VkSimpleCallback<List<VkTrack>>() {
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

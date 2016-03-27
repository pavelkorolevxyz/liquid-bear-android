package com.pillowapps.liqear.activities.modes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.ListBaseActivity;
import com.pillowapps.liqear.adapters.recyclers.TrackAdapter;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.models.lastfm.LastfmLibraryModel;

import java.util.List;

import javax.inject.Inject;

public class LastfmLibraryActivity extends ListBaseActivity {

    private TrackAdapter adapter;

    @Inject
    LastfmLibraryModel libraryModel;
    @Inject
    AuthorizationInfoManager authManager;

    public static Intent startIntent(Context context) {
        return new Intent(context, LastfmLibraryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);

        setTitle(getResources().getString(R.string.library));
        getLibraryTracks();
    }

    private void fillWithLastfmTracklist(List<LastfmTrack> lastfmTracks) {
        final List<Track> trackList = Converter.convertLastfmTrackList(lastfmTracks);
        if (adapter == null || adapter.getItemCount() == 0) {
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
        updateEmptyTextView();
    }

    private void getLibraryTracks() {
        libraryModel.getLibrary(authManager.getLastfmName(),   new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> data) {
                        fillWithLastfmTracklist(data);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
              );
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
                Toast.makeText(LastfmLibraryActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
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

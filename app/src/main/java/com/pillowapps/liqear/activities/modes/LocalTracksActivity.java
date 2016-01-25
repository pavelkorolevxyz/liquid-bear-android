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
import com.pillowapps.liqear.callbacks.LocalDataCallback;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.models.local.LocalAlbumModel;
import com.pillowapps.liqear.models.local.LocalArtistModel;
import com.pillowapps.liqear.models.local.LocalTrackModel;

import java.util.List;

public class LocalTracksActivity extends ListBaseActivity {

    private TrackAdapter adapter;
    private LocalDataCallback<List<Track>> tracksCallback = new LocalDataCallback<List<Track>>() {
        @Override
        public void success(List<Track> tracks) {
            fillWithTracklist(tracks);
        }

        @Override
        public void failure(String errorMessage) {
            progressBar.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        String albumId = null;
        String artistId = null;
        String filePath = null;
        if (extras != null) {
            albumId = extras.getString("albumId");
            artistId = extras.getString("artistId");
            filePath = extras.getString("filePath");
        }
        if (albumId == null && artistId == null && filePath == null) {
            loadTracks();
        } else if (albumId != null) {
            loadTracksFromLocalAlbum(albumId);
        } else if (artistId != null) {
            loadTracksFromLocalArtist(artistId);
        } else {
            loadTracksByFolder(filePath);
        }
        setTitle(getString(R.string.tracks));

        recycler.setOnCreateContextMenuListener(this);
    }

    private void fillWithTracklist(List<Track> trackList) {
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

    private void loadTracks() {
        new LocalTrackModel().getAllTracks(tracksCallback);
    }

    private void loadTracksFromLocalAlbum(String albumId) {
        new LocalAlbumModel().getTracksFromAlbum(albumId, tracksCallback);
    }

    private void loadTracksFromLocalArtist(String artistId) {
        new LocalArtistModel().getTracksFromArtist(artistId, tracksCallback);
    }

    private void loadTracksByFolder(String filePath) {
        //todo
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
                Toast.makeText(LocalTracksActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
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

package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.ListBaseActivity;
import com.pillowapps.liqear.adapters.TrackAdapter;
import com.pillowapps.liqear.components.OnRecyclerItemClickListener;
import com.pillowapps.liqear.components.OnRecyclerLongItemClickListener;
import com.pillowapps.liqear.entities.Track;

import java.util.ArrayList;
import java.util.List;

public class SetlistTracksActivity extends ListBaseActivity {

    private TrackAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        List<String> stringArrayList = extras.getStringArrayList("tracks");
        String artist = extras.getString("artist");
        actionBar.setTitle(extras.getString("notation"));
        List<Track> tracks = new ArrayList<Track>();
        for (String trackTitle : stringArrayList) {
            tracks.add(new Track(artist, trackTitle));
        }

        fillWithVkTracklist(tracks);
    }

    private void fillWithVkTracklist(List<Track> trackList) {
        if (adapter == null || adapter.getItemCount() == 0) {
            emptyTextView.setVisibility(trackList.size() == 0 ? View.VISIBLE : View.GONE);
            adapter = new TrackAdapter(this, trackList, new OnRecyclerItemClickListener() {
                @Override
                public void onItemClicked(View view, int position) {
                    openMainPlaylist(adapter.getItems(), position, getToolbarTitle());
                }
            }, new OnRecyclerLongItemClickListener() {
                @Override
                public boolean onItemLongClicked(View view, int position) {
                    trackLongClick(adapter.getItems(), position);
                    return true;
                }
            });
            recycler.setAdapter(adapter);
        } else {
            adapter.addAll(trackList);
        }
        progressBar.setVisibility(View.GONE);
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
                Toast.makeText(SetlistTracksActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
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

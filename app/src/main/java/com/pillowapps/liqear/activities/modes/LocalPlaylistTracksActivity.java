package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.adapters.TrackAdapter;
import com.pillowapps.liqear.callbacks.GetPlaylistCallback;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.TrackUtils;
import com.pillowapps.liqear.models.PlaylistModel;

import java.util.List;

public class LocalPlaylistTracksActivity extends ListBaseActivity {

    private TrackAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        String title = extras.getString("title");
        actionBar.setTitle(title);

        loadPlaylistTracks(extras.getLong("pid"));

        recyclerView.setOnCreateContextMenuListener(this);
    }

    private void fillWithTracklist(List<Track> trackList) {
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

    private void loadPlaylistTracks(long playlistId) {
        new PlaylistModel().getPlaylist(playlistId, new GetPlaylistCallback() {
            @Override
            public void onCompleted(Playlist playlist) {
                List<Track> tracks = playlist.getTracks();
                fillWithTracklist(tracks);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v == recyclerView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Track track = adapter.getItem(info.position);
            menu.setHeaderTitle(TrackUtils.getNotation(track));
            String[] menuItems = getResources()
                    .getStringArray(R.array.playlist_tracklist_item_menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }
}

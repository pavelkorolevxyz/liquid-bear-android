package com.pillowapps.liqear.activities.modes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.ListBaseActivity;
import com.pillowapps.liqear.adapters.recyclers.TrackAdapter;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.TrackUtils;
import com.pillowapps.liqear.models.PlaylistModel;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LocalPlaylistTracksActivity extends ListBaseActivity {

    private TrackAdapter adapter;

    @Inject
    PlaylistModel playlistModel;

    public static Intent startIntent(Context context) {
        return new Intent(context, LocalPlaylistTracksActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);

        Bundle extras = getIntent().getExtras();
        String title = extras.getString("title");
        setTitle(title);

        loadPlaylistTracks(extras.getLong("pid"));

        recycler.setOnCreateContextMenuListener(this);
    }

    private void fillWithTracklist(List<Track> trackList) {
        if (adapter == null || adapter.getItemCount() == 0) {
            adapter = new TrackAdapter(getApplicationContext(), trackList);
            recycler.setAdapter(adapter);
        } else {
            adapter.addAll(trackList);
        }

//        recycler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                openMainPlaylist(adapter.getItems(), position, getToolbarTitle());
//            }
//        });
//
//        recycler.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                trackLongClick(adapter.getItems(), position);
//                return true;
//            }
//        });

        progressBar.setVisibility(View.GONE);
        updateEmptyTextView();
    }

    private void loadPlaylistTracks(Long playlistId) {
        playlistModel.getPlaylist(playlistId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(playlist -> {
                    fillWithTracklist(playlist.getTracks());
                });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v == recycler) {
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

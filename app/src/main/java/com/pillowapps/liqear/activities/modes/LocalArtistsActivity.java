package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.view.View;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.ListBaseActivity;
import com.pillowapps.liqear.adapters.recyclers.ArtistAdapter;
import com.pillowapps.liqear.callbacks.LocalDataCallback;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.models.local.LocalArtistModel;

import java.util.List;

public class LocalArtistsActivity extends ListBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setTitle(getString(R.string.artist));
        loadLocalArtists();

        recycler.setOnCreateContextMenuListener(this);
    }

    private void fillWithArtists(List<Artist> artists) {
        emptyTextView.setVisibility(artists.size() == 0 ? View.VISIBLE : View.GONE);
        ArtistAdapter adapter = new ArtistAdapter(artists, (view, position) -> {
            //todo
        });
        recycler.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    private void loadLocalArtists() {
        new LocalArtistModel().getAllArtists(new LocalDataCallback<List<Artist>>() {
            @Override
            public void success(List<Artist> data) {
                fillWithArtists(data);
            }

            @Override
            public void failure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}

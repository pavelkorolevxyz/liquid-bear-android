package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.view.View;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.ListBaseActivity;
import com.pillowapps.liqear.adapters.recyclers.ArtistAdapter;
import com.pillowapps.liqear.callbacks.LocalDataCallback;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.helpers.PreferencesScreenManager;
import com.pillowapps.liqear.models.local.LocalArtistModel;

import java.util.List;

import javax.inject.Inject;

public class LocalArtistsActivity extends ListBaseActivity {

    @Inject
    PreferencesScreenManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);

        setTitle(getString(R.string.artist));
        loadLocalArtists();

        recycler.setOnCreateContextMenuListener(this);
    }

    private void fillWithArtists(List<Artist> artists) {
        ArtistAdapter adapter = new ArtistAdapter(artists, preferencesManager.isDownloadImagesEnabled(), (view, position) -> {
            //todo
        });
        recycler.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        updateEmptyTextView();
    }

    private void loadLocalArtists() {
        new LocalArtistModel(this).getAllArtists(new LocalDataCallback<List<Artist>>() {
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

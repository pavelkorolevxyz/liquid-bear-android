package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.view.View;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.ListBaseActivity;
import com.pillowapps.liqear.adapters.recyclers.AlbumAdapter;
import com.pillowapps.liqear.callbacks.LocalDataCallback;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.helpers.PreferencesScreenManager;
import com.pillowapps.liqear.models.local.LocalAlbumModel;

import java.util.List;

import javax.inject.Inject;

public class LocalAlbumsActivity extends ListBaseActivity {

    @Inject
    PreferencesScreenManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);

        setTitle(getString(R.string.album));
        loadLocalAlbums();

        recycler.setOnCreateContextMenuListener(this);
    }

    private void fillWithAlbums(List<Album> albums) {
        AlbumAdapter adapter = new AlbumAdapter(albums, preferencesManager.isDownloadImagesEnabled(), (view, position) -> {
            //todo
        });
        recycler.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        updateEmptyTextView();
    }

    private void loadLocalAlbums() {
        new LocalAlbumModel(this).getAllAlbums(new LocalDataCallback<List<Album>>() {
            @Override
            public void success(List<Album> data) {
                fillWithAlbums(data);
            }

            @Override
            public void failure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}

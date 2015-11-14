package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.view.View;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.ListBaseActivity;
import com.pillowapps.liqear.adapters.AlbumAdapter;
import com.pillowapps.liqear.callbacks.LocalDataCallback;
import com.pillowapps.liqear.components.OnRecyclerItemClickListener;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.models.local.LocalAlbumModel;

import java.util.List;

public class LocalAlbumsActivity extends ListBaseActivity {

    private AlbumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setTitle(getString(R.string.album));
        loadLocalAlbums();

        recycler.setOnCreateContextMenuListener(this);
    }

    private void fillWithAlbums(List<Album> albums) {
        emptyTextView.setVisibility(albums.size() == 0 ? View.VISIBLE : View.GONE);
        adapter = new AlbumAdapter(albums, new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                //todo
            }
        });
        recycler.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    private void loadLocalAlbums() {
        new LocalAlbumModel().getAllAlbums(new LocalDataCallback<List<Album>>() {
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

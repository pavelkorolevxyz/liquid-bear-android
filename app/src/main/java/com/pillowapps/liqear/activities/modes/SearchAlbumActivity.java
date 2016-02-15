package com.pillowapps.liqear.activities.modes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.ImagePagerActivity;
import com.pillowapps.liqear.activities.base.SearchListBaseActivity;
import com.pillowapps.liqear.adapters.recyclers.AlbumAdapter;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.DelayedTextWatcher;
import com.pillowapps.liqear.helpers.PreferencesScreenManager;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.models.lastfm.LastfmAlbumModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.inject.Inject;

public class SearchAlbumActivity extends SearchListBaseActivity {

    private AlbumAdapter adapter;

    @Inject
    LastfmAlbumModel lastfmAlbumModel;
    @Inject
    PreferencesScreenManager preferencesManager;

    public static Intent startIntent(Context context) {
        return new Intent(context, SearchAlbumActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);

        setTitle(getString(R.string.album));
        editText.setHint(getString(R.string.album));
        editText.setFloatingLabelText(getString(R.string.album));
        progressBar.setVisibility(View.GONE);
        loadAlbumPresets();

        initWatcher();
    }

    protected void initWatcher() {
        editText.addTextChangedListener(new DelayedTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                runTaskWithDelay(() -> {
                    String searchQuery = editText.getText().toString().trim();
                    if (searchQuery.length() == 0) {
                        loadAlbumPresets();
                        return;
                    }
                    searchAlbum(searchQuery, getPageSize(), 1);
                });
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadAlbumPresets() {
        LinkedHashSet<Album> albums = new LinkedHashSet<>(Constants.PRESET_WANTED_COUNT);
        SharedPreferences artistPreferences = SharedPreferencesManager.getAlbumPreferences(this);
        int albumCount = artistPreferences.getInt(Constants.PRESET_LAST_NUMBER, 0);
        if (albumCount >= Constants.PRESET_WANTED_COUNT) {
            for (int i = albumCount - 1; i >= albumCount - Constants.PRESET_WANTED_COUNT; i--) {
                albums.add(new Album(
                        artistPreferences.getString(Constants.ALBUM_ARTIST_NUMBER + (i % Constants.PRESET_WANTED_COUNT), ""),
                        artistPreferences.getString(Constants.ALBUM_TITLE_NUMBER + (i % Constants.PRESET_WANTED_COUNT), ""),
                        null,
                        null,
                        artistPreferences.getString(Constants.IMAGE + (i % Constants.PRESET_WANTED_COUNT), "")
                ));
            }
        } else {
            for (int i = albumCount - 1; i >= 0; i--) {
                albums.add(new Album(
                        artistPreferences.getString(Constants.ALBUM_ARTIST_NUMBER + i, ""),
                        artistPreferences.getString(Constants.ALBUM_TITLE_NUMBER + i, ""),
                        null,
                        null,
                        artistPreferences.getString(Constants.IMAGE + i, "")
                ));
            }
        }
        fillWithAlbums(new ArrayList<>(albums));
    }

    private void fillWithAlbums(List<Album> albums) {
        adapter = new AlbumAdapter(albums, preferencesManager.isDownloadImagesEnabled(), (view, position) -> {
            SharedPreferences albumPreferences = SharedPreferencesManager.getAlbumPreferences(this);
            SharedPreferences.Editor editor = albumPreferences.edit();
            int albumLastNumberAll = albumPreferences.getInt(Constants.PRESET_LAST_NUMBER, 0);
            int albumsLastNumberMod = albumLastNumberAll % Constants.PRESET_WANTED_COUNT;
            editor.putInt(Constants.PRESET_LAST_NUMBER, albumLastNumberAll + 1);
            Album album = adapter.getItem(position);
            editor.putString(Constants.ALBUM_ARTIST_NUMBER + albumsLastNumberMod,
                    album.getArtist());
            editor.putString(Constants.ALBUM_TITLE_NUMBER + albumsLastNumberMod,
                    album.getTitle());
            editor.putString(Constants.IMAGE + albumsLastNumberMod, album.getImageUrl());
            editor.apply();
            openLastfmAlbum(album);
        });
        recycler.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        updateEmptyTextView();
    }

    private void searchAlbum(String searchQuery, int limit, int page) {
        lastfmAlbumModel.searchAlbum(searchQuery, limit, page,
                new SimpleCallback<List<LastfmAlbum>>() {
                    @Override
                    public void success(List<LastfmAlbum> lastfmAlbums) {
                        progressBar.setVisibility(View.GONE);
                        fillWithAlbums(Converter.convertAlbums(lastfmAlbums));
                    }

                    @Override
                    public void failure(String error) {
                        showError(error);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}

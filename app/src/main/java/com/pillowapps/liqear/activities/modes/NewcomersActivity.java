package com.pillowapps.liqear.activities.modes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.adapters.NewcomersAdapter;
import com.pillowapps.liqear.callbacks.NewcomersSimpleCallback;
import com.pillowapps.liqear.components.LoadMoreRecyclerView;
import com.pillowapps.liqear.components.OnLoadMoreListener;
import com.pillowapps.liqear.components.ResultActivity;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.models.portals.AlterportalAlbumModel;
import com.pillowapps.liqear.models.portals.FunkySoulsAlbumModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewcomersActivity extends ResultActivity {
    public static final String MODE = "mode";
    public static final int NEWCOMERS_START_ITEMS = 20;
    private Mode mode;
    private NewcomersAdapter adapter;
    private ProgressBar progressBar;
    private int visiblePages = 1;
    private LoadMoreRecyclerView recycler;
    private TextView emptyTextView;
    private OnRecyclerItemClickListener clickListener = new OnRecyclerItemClickListener() {
        @Override
        public void onItemClicked(View view, int position) {
            Intent intent = new Intent(NewcomersActivity.this,
                    NewcomersTracksActivity.class);
            Album album = adapter.getItem(position);
            intent.putExtra("artist", album.getArtist());
            intent.putStringArrayListExtra("tracks", new ArrayList<>(album.getTracks()));
            intent.putExtra("title", album.getTitle());
            startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                Intent intent = new Intent(NewcomersActivity.this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case 0:
                String url = null;
                switch (mode) {
                    case FUNKYSOULS:
                        url = "http://funkysouls.com";
                        break;
                    case ALTERPORTAL:
                        url = "http://alterportal.ru";
                        break;
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.play:
                if (adapter == null) break;
                List<Album> albumsInList = adapter.getItems();
                List<Track> tracks = new ArrayList<Track>(albumsInList.size() * 10);
                for (Album album : albumsInList) {
                    for (String title : album.getTracks()) {
                        tracks.add(new Track(album.getArtist(), title));
                    }
                }
                openMainPlaylist(tracks, 0, getToolbarTitle());
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.play_albums_menu, menu);
        MenuItem item = null;
        switch (mode) {
            case FUNKYSOULS:
                item = menu.add(getResources().getString(R.string.funkysouls));
                break;
            case ALTERPORTAL:
                item = menu.add(getResources().getString(R.string.alterportal));
                break;
        }
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newcomers_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.newcomers));

        final LinearLayout searchLinearLayout =
                (LinearLayout) findViewById(R.id.edit_part_quick_search_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        searchLinearLayout.setVisibility(View.GONE);
        mode = (Mode) getIntent().getSerializableExtra(MODE);
        recycler = (LoadMoreRecyclerView) findViewById(R.id.list);
        emptyTextView = (TextView) findViewById(R.id.empty);
        switch (mode) {
            case FUNKYSOULS:
                getNewcomersFunky(visiblePages++);
                actionBar.setTitle(getResources().getString(R.string.funkysouls));
                break;
            case ALTERPORTAL:
                getNewcomersAlterportal(visiblePages++);
                actionBar.setTitle(getResources().getString(R.string.alterportal));
                break;
            default:
                break;
        }
        initUi();
        initListeners();
    }

    private void initUi() {
        recycler.enableLoadMore(true);
    }

    private void initListeners() {
        recycler.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                switch (mode) {
                    case FUNKYSOULS:
                        getNewcomersFunky(visiblePages++);
                        break;
                    case ALTERPORTAL:
                        getNewcomersAlterportal(visiblePages++);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void getNewcomersFunky(int page) {
        new FunkySoulsAlbumModel().getNewcomers(Arrays.asList(page), new NewcomersSimpleCallback<List<Album>>() {
            @Override
            public void success(List<Album> albums) {
                fillAlbums(albums);
//                recycler.onLoadMoreComplete();
                if (adapter.getItemCount() < NEWCOMERS_START_ITEMS) {
                    getNewcomersFunky(visiblePages++);
                }
            }

            @Override
            public void failure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getNewcomersAlterportal(int page) {
        new AlterportalAlbumModel().getNewcomers(Arrays.asList(page),
                new NewcomersSimpleCallback<List<Album>>() {
                    @Override
                    public void success(List<Album> albums) {
                        fillAlbums(albums);
                        if (adapter.getItemCount() < NEWCOMERS_START_ITEMS) {
                            getNewcomersAlterportal(visiblePages++);
                        }
                    }

                    @Override
                    public void failure(String errorMessage) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void fillAlbums(List<Album> albums) {
        if (adapter == null) {
            if (albums.size() == 0) {
                emptyTextView.setVisibility(View.VISIBLE);
            }
            adapter = new NewcomersAdapter(albums, clickListener);
            recycler.setAdapter(adapter);
        } else {
            adapter.getItems().addAll(albums);
            adapter.notifyDataSetChanged();
        }
        progressBar.setVisibility(View.GONE);
    }

    public enum Mode {
        FUNKYSOULS,
        ALTERPORTAL
    }
}

package com.pillowapps.liqear.activities.modes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.viewers.LastfmArtistViewerActivity;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.components.ResultActivity;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.models.lastfm.LastfmRecommendationsModel;
import com.pillowapps.liqear.models.lastfm.LastfmUserModel;

import java.util.List;

import javax.inject.Inject;

public class LastfmRecommendationsActivity extends ResultActivity {
    private static final int RECOMMENDATIONS_AMOUNT = 20;
    private RecommendationsArrayAdapter<Artist> adapter;
    private ProgressBar progressBar;
    private GridView gridView;
    private int loadedArtists = 0;
    private int loadedPages = 0;
    private boolean loading = false;
    private ListView listView;
    private boolean gridMode = true;

    @Inject
    LastfmUserModel lastfmUserModel;

    @Inject
    LastfmRecommendationsModel lastfmRecommendationsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);

        setContentView(R.layout.list_grid_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.recommendations));
        }
        initUi();
        initListeners();
        getRecommendedArtists(RECOMMENDATIONS_AMOUNT, loadedPages + 1);
    }

    private void initUi() {
        progressBar = (ProgressBar) findViewById(R.id.pageProgressBar);
        gridView = (GridView) findViewById(R.id.recommendations_grid_view);
        listView = (ListView) findViewById(R.id.recommendations_list_view);
        if (!SharedPreferencesManager.getPreferences().getBoolean("show_images_grid", true)) {
            listView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            gridMode = false;
        }
    }

    private void initListeners() {
        AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // No operations.
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount && !loading && adapter != null
                        && loadedArtists == adapter.getCount()) {
                    getRecommendedArtists(RECOMMENDATIONS_AMOUNT, loadedPages + 1);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        };
        gridView.setOnScrollListener(scrollListener);
        listView.setOnScrollListener(scrollListener);
        AdapterView.OnItemClickListener clickListener = (adapterView, view, i, l) -> {
            Intent artistInfoIntent = new Intent(LastfmRecommendationsActivity.this,
                    LastfmArtistViewerActivity.class);
            artistInfoIntent.putExtra(LastfmArtistViewerActivity.ARTIST, adapter.get(i).getName());
            startActivityForResult(artistInfoIntent, Constants.MAIN_REQUEST_CODE);
        };
        gridView.setOnItemClickListener(clickListener);
        listView.setOnItemClickListener(clickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(getResources().getString(R.string.compilation));
        MenuItemCompat.setShowAsAction(item, 0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
            case 0:
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    Toast.makeText(LastfmRecommendationsActivity.this,
                            R.string.vk_not_authorized, Toast.LENGTH_SHORT).show();
                    break;
                }
                if (adapter != null) {
                    List<Artist> values = adapter.getValues();
                    getRecommendedTracks(values);
                }
                progressBar.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        return true;
    }

    private void getRecommendedArtists(int limit, int page) {
        loading = true;
        progressBar.setVisibility(View.VISIBLE);
        lastfmUserModel.getUserRecommendedArtists(limit, page, new SimpleCallback<List<LastfmArtist>>() {
            @Override
            public void success(List<LastfmArtist> data) {
                progressBar.setVisibility(View.GONE);
                List<Artist> artists = Converter.convertArtistList(data);
                if (adapter == null) {
                    adapter = new RecommendationsArrayAdapter<>(LastfmRecommendationsActivity.this, artists);
                    if (gridMode) {
                        gridView.setAdapter(adapter);
                    } else {
                        listView.setAdapter(adapter);
                    }
                } else {
                    adapter.addValues(artists);
                    adapter.notifyDataSetChanged();
                }
                loadedArtists += RECOMMENDATIONS_AMOUNT;
                loadedPages++;
                loading = false;
            }

            @Override
            public void failure(String errorMessage) {
                ErrorNotifier.showError(LastfmRecommendationsActivity.this, errorMessage);
            }
        });
    }

    private void getRecommendedTracks(List<Artist> artists) {
        lastfmRecommendationsModel.getRecommendationsTracks(artists, new SimpleCallback<List<LastfmTrack>>() {
            @Override
            public void success(List<LastfmTrack> data) {
                openMainPlaylist(Converter.convertLastfmTrackList(data), 0, getToolbarTitle());
            }

            @Override
            public void failure(String errorMessage) {

            }
        });
    }

    static class ViewHolder {
        TextView text;
        ImageView image;
        boolean loadImages;
    }

    private class RecommendationsArrayAdapter<T> extends ArrayAdapter<T> {
        private final List<T> values;

        public RecommendationsArrayAdapter(Context context, List<T> values) {
            super(context, R.layout.image_text_tile, values);
            this.values = values;
        }

        public T get(int position) {
            return values.get(position);
        }

        public List<T> getValues() {
            return values;
        }

        public void addValues(List<T> newValues) {
            values.addAll(newValues);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            LayoutInflater inflater = getLayoutInflater();
            Artist artist = (Artist) values.get(position);
            if (gridMode) {
                if (convertView == null) {
                    convertView = View.inflate(LastfmRecommendationsActivity.this, R.layout.image_text_tile, null);
                    holder = new ViewHolder();
                    holder.text = (TextView) convertView.findViewById(R.id.text_tile_list_item);
                    holder.image = (ImageView) convertView.findViewById(R.id.image_tile_list_item);
                    holder.loadImages = SharedPreferencesManager.getPreferences()
                            .getBoolean("download_images_check_box_preferences", true);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                    holder.image.setImageBitmap(null);
                }
                holder.text.setText(artist.getName());
                holder.text.setBackgroundColor(ContextCompat.getColor(LastfmRecommendationsActivity.this, R.color.accent));
                if (holder.loadImages) {
                    new ImageModel().loadImage(artist.getPreviewUrl(), holder.image, bitmap -> {
                        Palette.Swatch vibrantSwatch = new Palette.Builder(bitmap).generate().getVibrantSwatch(); // todo async
                        if (vibrantSwatch == null) return;
                        holder.text.setBackgroundColor(vibrantSwatch.getRgb());
                    });
                } else {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.image_list_item, null);
                    holder = new ViewHolder();
                    holder.text = (TextView) convertView.findViewById(R.id.text_list_item);
                    holder.image = (ImageView) convertView.findViewById(R.id.image_view_list_item);
                    holder.loadImages = SharedPreferencesManager.getPreferences()
                            .getBoolean("download_images_check_box_preferences", true);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                    holder.image.setImageBitmap(null);
                }
                holder.text = (TextView) convertView.findViewById(R.id.text_list_item);
                holder.text.setText(artist.getName());
                if (holder.loadImages) {
                    new ImageModel().loadImage(artist.getPreviewUrl(), holder.image);
                } else {
                    holder.image.setVisibility(View.GONE);
                }
            }
            return convertView;
        }

    }
}

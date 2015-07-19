package com.pillowapps.liqear.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.viewers.LastfmArtistViewerActivity;
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
import com.pillowapps.liqear.network.ImageLoadingListener;
import com.pillowapps.liqear.callbacks.SimpleCallback;

import java.util.List;

public class RecommendationsActivity extends ResultActivity {
    private static final int RECOMMENDATIONS_AMOUNT = 20;
    private RecommendationsArrayAdapter<Artist> adapter;
    private ProgressBar progressBar;
    private GridView gridView;
    private int loadedArtists = 0;
    private int loadedPages = 0;
    private boolean loading = false;
    private ListView listView;
    private boolean gridMode = true;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisc()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new FadeInBitmapDisplayer(500))
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_grid_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.recommendations));
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
        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent artistInfoIntent = new Intent(RecommendationsActivity.this,
                        LastfmArtistViewerActivity.class);
                artistInfoIntent.putExtra(LastfmArtistViewerActivity.ARTIST, adapter.get(i).getName());
                startActivityForResult(artistInfoIntent, Constants.MAIN_REQUEST_CODE);
            }
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
                    Toast.makeText(RecommendationsActivity.this,
                            R.string.vk_not_authorized, Toast.LENGTH_SHORT).show();
                    break;
                }
                if (adapter != null) {
                    List values = adapter.getValues();
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
        new LastfmUserModel().getUserRecommendedArtists(limit, page, new SimpleCallback<List<LastfmArtist>>() {
            @Override
            public void success(List<LastfmArtist> data) {
                progressBar.setVisibility(View.GONE);
                List<Artist> artists = Converter.convertArtistList(data);
                if (adapter == null) {
                    adapter = new RecommendationsArrayAdapter<>(RecommendationsActivity.this, artists, Artist.class);
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
                ErrorNotifier.showError(RecommendationsActivity.this, errorMessage);
            }
        });
    }

    private void getRecommendedTracks(List<Artist> artists) {
        new LastfmRecommendationsModel().getRecommendationsTracks(artists, new SimpleCallback<List<LastfmTrack>>() {
            @Override
            public void success(List<LastfmTrack> data) {
                openMainPlaylist(Converter.convertLastfmTrackList(data), 0);
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
        private final Class<T> clazz;

        public RecommendationsArrayAdapter(Context context, List<T> values, Class<T> clazz) {
            super(context, R.layout.image_text_tile, values);
            this.values = values;
            this.clazz = clazz;
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
                    convertView = View.inflate(RecommendationsActivity.this, R.layout.image_text_tile, null);
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
                holder.text.setBackgroundColor(getResources().getColor(R.color.accent));
                if (holder.loadImages) {
                    new ImageModel().loadImage(artist.getPreviewUrl(), holder.image, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted() {

                        }

                        @Override
                        public void onLoadingFailed(String message) {

                        }

                        @Override
                        public void onLoadingComplete(Bitmap bitmap) {
                            Palette.generateAsync(bitmap,
                                    new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(Palette palette) {
                                            Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                                            if (vibrantSwatch == null) return;
                                            holder.text.setBackgroundColor(
                                                    vibrantSwatch.getRgb());
                                        }
                                    });
                        }

                        @Override
                        public void onLoadingCancelled() {

                        }
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
                    imageLoader.displayImage(artist.getPreviewUrl(), holder.image, options);
                } else {
                    holder.image.setVisibility(View.GONE);
                }
            }
            return convertView;
        }

    }
}

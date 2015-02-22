package com.pillowapps.liqear.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.MenuCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.ResultSherlockActivity;
import com.pillowapps.liqear.connection.GetResponseCallback;
import com.pillowapps.liqear.connection.Params;
import com.pillowapps.liqear.connection.QueryManager;
import com.pillowapps.liqear.connection.ReadyResult;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.models.Artist;
import com.pillowapps.liqear.models.Track;

import java.util.List;

public class RecommendationsSherlockActivity extends ResultSherlockActivity {
    private static final int RECOMMENDATIONS_AMOUNT = 100;
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
        if (!PreferencesManager.getPreferences().getBoolean("show_images_grid", true)) {
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
                Intent artistInfoIntent = new Intent(RecommendationsSherlockActivity.this,
                        ArtistViewerActivity.class);
                artistInfoIntent.putExtra(ArtistViewerActivity.ARTIST, adapter.get(i).getName());
                startActivityForResult(artistInfoIntent, Constants.MAIN_REQUEST_CODE);
            }
        };
        gridView.setOnItemClickListener(clickListener);
        listView.setOnItemClickListener(clickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(getResources().getString(R.string.compilation));
        MenuCompat.setShowAsAction(item, MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
            case 0:
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    Toast.makeText(RecommendationsSherlockActivity.this,
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
        QueryManager.getInstance().getRecommendedArtists(limit, page, new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (checkForError(result, Params.ApiSource.LASTFM)) return;
                final Object object = result.getObject();
                progressBar.setVisibility(View.GONE);
                if (adapter == null) {
                    adapter = new RecommendationsArrayAdapter<Artist>(RecommendationsSherlockActivity.this, (List) object, Artist.class);
                    if (gridMode) {
                        gridView.setAdapter(adapter);
                    } else {
                        listView.setAdapter(adapter);
                    }
                } else {
                    adapter.addValues((List) object);
                    adapter.notifyDataSetChanged();
                }
                loadedArtists += RECOMMENDATIONS_AMOUNT;
                loadedPages++;
                loading = false;
            }
        });
    }

    private void getRecommendedTracks(List<Artist> artists) {
        QueryManager.getInstance().getRecommendedTracks(artists, new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (checkForError(result, Params.ApiSource.LASTFM)) return;
                openMainPlaylist((List<Track>) result.getObject(), 0);
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
                    convertView = inflater.inflate(R.layout.image_text_tile, null);
                    holder = new ViewHolder();
                    holder.text = (TextView) convertView.findViewById(R.id.text_tile_list_item);
                    holder.image = (ImageView) convertView.findViewById(R.id.image_tile_list_item);
                    holder.loadImages = PreferencesManager.getPreferences()
                            .getBoolean("download_images_check_box_preferences", true);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                    holder.image.setImageBitmap(null);
                }
                holder.text.setText(artist.getName());
                if (holder.loadImages) {
                    imageLoader.displayImage(artist.getPreviewUrl(), holder.image, options,
                            new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted() {

                                }

                                @Override
                                public void onLoadingFailed(FailReason failReason) {

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
                    holder.loadImages = PreferencesManager.getPreferences()
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

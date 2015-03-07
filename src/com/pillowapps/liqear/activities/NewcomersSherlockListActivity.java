package com.pillowapps.liqear.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.costum.android.widget.LoadMoreListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.ResultSherlockActivity;
import com.pillowapps.liqear.network.GetResponseCallback;
import com.pillowapps.liqear.network.QueryManager;
import com.pillowapps.liqear.network.ReadyResult;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Track;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public class NewcomersSherlockListActivity extends ResultSherlockActivity {
    public static final String MODE = "mode";
    public static final int NEWCOMERS_START_ITEMS = 20;
    private Mode mode;
    private NewcomersAdapter adapter;
    private ProgressBar progressBar;
    private int visiblePages = 1;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisc()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new FadeInBitmapDisplayer(500))
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private LoadMoreListView listView;
    private TextView emptyTextView;

    /**
     * Calls on Sherlock button pressed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                Intent intent = new Intent(NewcomersSherlockListActivity.this, MainActivity.class);
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
                    case POST_HARDCORE_RU:
                        url = "http://post-hardcore.ru";
                        break;
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.play:
                if (adapter == null) break;
                List<Album> albumsInList = adapter.getValues();
                List<Track> tracks = new ArrayList<Track>(albumsInList.size() * 10);
                for (Album album : albumsInList) {
                    for (String title : album.getTracks()) {
                        tracks.add(new Track(album.getArtist(), title));
                    }
                }
                openMainPlaylist(tracks, 0);
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
            case POST_HARDCORE_RU:
                item = menu.add(getResources().getString(R.string.post_hardcore_ru));
                break;
        }
        MenuItemCompat.setShowAsAction(item,
                MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.newcomers));

        final LinearLayout searchLinearLayout =
                (LinearLayout) findViewById(R.id.edit_part_quick_search_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        searchLinearLayout.setVisibility(View.GONE);
        mode = (Mode) getIntent().getSerializableExtra(MODE);
        listView = (LoadMoreListView) findViewById(R.id.list);
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
        initListeners();
    }

    private void initListeners() {
        listView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NewcomersSherlockListActivity.this,
                        SearchSherlockListActivity.class);
                Album album = (Album) adapter.getValues().get(position);
                intent.putExtra("artist", album.getArtist());
                intent.putStringArrayListExtra("tracks", new ArrayList<String>(album.getTracks()));
                intent.putExtra("title", album.getTitle());
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.TRACKLIST_FUNKY);
                startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
            }
        });
    }

    private void showException(String error, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.error) + " " + error.substring(error.indexOf(' ') + 1));
        builder.setMessage(message);
        builder.setNeutralButton(getResources().getString(android.R.string.ok), null);
        AlertDialog exceptionDialog = builder.create();
        exceptionDialog.show();
    }

    private boolean checkForError(ReadyResult result) {
        final boolean error = !result.isOk();
        if (error) {
            showException(result.getMethod() + " " + result.getErrorCode(),
                    (String) result.getObject());
            progressBar.setVisibility(View.GONE);
        }
        return error;
    }

    private void getNewcomersFunky(int page) {
        QueryManager.getInstance().getNewcomersFunky(Arrays.asList(page), new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (checkForError(result)) return;
                final Object object = result.getObject();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<Album> albums = (List<Album>) object;
                        fillAlbums(albums);
                        listView.onLoadMoreComplete();
                        if (listView.getCount() < NEWCOMERS_START_ITEMS) {
                            getNewcomersFunky(visiblePages++);
                        }
                    }
                });
            }
        });
    }

    private void getNewcomersAlterportal(int page) {
        QueryManager.getInstance().getNewcomersAlterportal(Arrays.asList(page),
                new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (checkForError(result)) return;
                final Object object = result.getObject();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<Album> albums = (List<Album>) object;
                        fillAlbums(albums);
                        listView.onLoadMoreComplete();
                        if (listView.getCount() < NEWCOMERS_START_ITEMS) {
                            getNewcomersAlterportal(visiblePages++);
                        }
                    }
                });
            }
        });
    }

    private void fillAlbums(List<Album> albums) {
        if (adapter == null) {
            if (albums.size() == 0) {
                emptyTextView.setVisibility(View.VISIBLE);
            }
            adapter = new NewcomersAdapter<Album>(this, albums);
            listView.setAdapter(adapter);
        } else {
            adapter.getValues().addAll(albums);
            adapter.notifyDataSetChanged();
        }
        progressBar.setVisibility(View.GONE);
    }

    public enum Mode {
        FUNKYSOULS,
        ALTERPORTAL,
        POST_HARDCORE_RU
    }

    static class ViewHolder {

        TextView artistAlbum;
        TextView genre;
        ImageView cover;
        boolean loadImages;
    }

    private class NewcomersAdapter<T> extends ArrayAdapter<T> {

        private final Context context;
        private final List<T> values;

        public NewcomersAdapter(Context context, List<T> values) {
            super(context, R.layout.album_item, values);
            this.context = context;
            this.values = values;
        }

        public T get(int position) {
            return values.get(position);
        }

        public List<T> getValues() {
            return values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.album_item, null);
                holder = new ViewHolder();
                holder.artistAlbum = (TextView) convertView.findViewById(R.id.text_list_item);
                holder.genre = (TextView) convertView.findViewById(R.id.genre_list_item);
                holder.cover = (ImageView) convertView.findViewById(
                        R.id.cover_image_view_album_list_item);
                holder.loadImages = PreferencesManager.getPreferences()
                        .getBoolean("download_images_check_box_preferences", true);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.cover.setImageBitmap(null);
            }

            Album album = (Album) values.get(position);
            holder.artistAlbum.setText(Html.fromHtml(album.getNotation()));
            holder.genre.setText(Html.fromHtml(album.getGenre()));
            if (holder.loadImages) {
                imageLoader.displayImage(album.getImageUrl(), holder.cover, options);
            } else {
                holder.cover.setVisibility(View.GONE);
            }
            return convertView;
        }

    }
}

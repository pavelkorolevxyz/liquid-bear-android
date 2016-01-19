package com.pillowapps.liqear.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.LocalPlaylistTracksActivity;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.components.HintMaterialEditText;
import com.pillowapps.liqear.components.ResultActivity;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.models.PlaylistModel;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsActivity extends ResultActivity {
    public static final String AIM = "aim";
    private PlaylistsArrayAdapter adapter;
    private Bundle extras;
    private Aim aim;
    private ListView listView;
    private TextView emptyTextView;

    public void onStart() {
        super.onStart();
        switch (aim) {
            case SAVE_AS_PLAYLIST: {
                saveAsPlaylist(false, -1);
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlists_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        extras = getIntent().getExtras();
        aim = (Aim) extras.getSerializable(AIM);
        listView = (ListView) findViewById(android.R.id.list);
        emptyTextView = (TextView) findViewById(R.id.empty);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.playlist_tab));
        }
        listView.setOnCreateContextMenuListener(this);

        new PlaylistModel().getSavedPlaylists(playlistList -> {
            adapter = new PlaylistsArrayAdapter(PlaylistsActivity.this, playlistList);
            runOnUiThread(() -> {
                listView.setAdapter(adapter);
                updateEmptyTextView();
            });
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            switch (aim) {
                case ADD_TO_PLAYLIST: {
                    Playlist playlist = adapter.get(position);
                    new PlaylistModel().addTrackToPlaylist(playlist,
                            new Track(extras.getString("artist"), extras.getString("title")));
                    finish();
                    break;
                }
                case SHOW_PLAYLISTS: {
                    Intent intent = new Intent(
                            PlaylistsActivity.this,
                            LocalPlaylistTracksActivity.class);
                    Playlist playlist = adapter.get(position);
                    intent.putExtra("title", playlist.getTitle());
                    intent.putExtra("pid", playlist.getId());
                    startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                    break;
                }
                case SAVE_AS_PLAYLIST: {
                    saveAsPlaylist(false, -1);
                    break;
                }
            }
        });
    }

    private void updateEmptyTextView() {
        emptyTextView.setVisibility(
                listView.getAdapter() != null
                        && listView.getAdapter().getCount() > 0
                        ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v.getId() == android.R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(adapter.getItem(info.position).getTitle());
            String[] menuItems = getResources().getStringArray(R.array.playlists_item_menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(android.view.Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Playlist playlist = adapter.getItem(info.position);
        switch (item.getItemId()) {
            case 0:
                adapter.getValues().remove(info.position);
                adapter.notifyDataSetChanged();
                updateEmptyTextView();
                new PlaylistModel().removePlaylist(playlist.getId());
                break;
            case 1:
                showSavePlaylistDialog(true, info.position);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlists_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
            case R.id.save_current_playlist:
                showSavePlaylistDialog(false, -1);
                break;
            case R.id.new_playlist:
                showNewDialog();
                break;
        }
        return true;
    }

    private void showNewDialog() {
        final HintMaterialEditText input = new HintMaterialEditText(this);
        input.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NORMAL);
        input.updateHint(getString(R.string.enter_title_here));

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.playlist)
                .customView(input, true)
                .positiveText(R.string.save)
                .negativeText(android.R.string.cancel)
                .build();

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
            String title = input.getText().toString();
            if (title.length() < 1) {
                title = getResources().getString(R.string.new_playlist);
            }
            long pid = new PlaylistModel().addPlaylist(title, new ArrayList<>());
            if (pid != -1) {
                adapter.getValues().add(0, new Playlist(pid, title));
                adapter.notifyDataSetChanged();
                updateEmptyTextView();
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showSavePlaylistDialog(final boolean isRenaming, final int position) {
        final HintMaterialEditText input = new HintMaterialEditText(this);
        input.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NORMAL);
        input.updateHint(getString(R.string.enter_title_here));

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.playlist)
                .customView(input, true)
                .positiveText(R.string.save)
                .negativeText(android.R.string.cancel)
                .build();

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
            String title = input.getText().toString();
            if (title.length() < 1) {
                title = Timeline.getInstance().getPlaylist().getTitle();
            }
            if (title.length() < 1) {
                title = getResources().getString(R.string.new_playlist);
            }

            if (isRenaming) {
                new PlaylistModel().renamePlaylist(
                        adapter.get(position).getId(), title);
                adapter.get(position).setTitle(title);
                adapter.notifyDataSetChanged();
                updateEmptyTextView();
            } else {
                long pid = new PlaylistModel().addPlaylist(title,
                        Timeline.getInstance().getPlaylistTracks());
                if (pid != -1) {
                    adapter.getValues().add(0, new Playlist(pid, title));
                    adapter.notifyDataSetChanged();
                    updateEmptyTextView();
                }
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void saveAsPlaylist(final boolean isRenaming, final int position) {
        final HintMaterialEditText input = new HintMaterialEditText(this);
        input.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NORMAL);
        input.updateHint(getString(R.string.enter_title_here));

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.playlist)
                .customView(input, true)
                .positiveText(R.string.save)
                .negativeText(android.R.string.cancel)
                .build();

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
            String title = input.getText().toString();
            if (title.length() < 1) {
                title = getResources().getString(R.string.new_playlist);
            }

            if (isRenaming) {
                new PlaylistModel().renamePlaylist(adapter.get(position).getId(), title);
                adapter.get(position).setTitle(title);
                adapter.notifyDataSetChanged();
                updateEmptyTextView();
            } else {
//                            List<Track> tracklist = getIntent()
//                                    .getParcelableArrayListExtra(Constants.TRACKLIST);
//                            long pid = new PlaylistModel().addPlaylist(title, tracklist);
//                            if (pid != -1) {
//                                ((List<Playlist>) adapter.getValues())
//                                        .add(0, new Playlist(pid, title));
//                                adapter.notifyDataSetChanged();
//                            }
//                            //todo with otto
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    public enum Aim {
        ADD_TO_PLAYLIST, SHOW_PLAYLISTS, SAVE_AS_PLAYLIST
    }

    private class PlaylistsArrayAdapter extends ArrayAdapter<Playlist> {
        private final Context context;
        private final List<Playlist> values;

        public PlaylistsArrayAdapter(Context context, List<Playlist> values) {
            super(context, R.layout.search_hint_item, values);
            this.context = context;
            this.values = values;
        }

        public Playlist get(int position) {
            return values.get(position);
        }

        public List<Playlist> getValues() {
            return values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.search_hint_item, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.text_list_item);
            textView.setText(values.get(position).getTitle());
            if (position % 2 == 0) {
                rowView.setBackgroundResource(R.drawable.list_item_background);
            } else {
                rowView.setBackgroundResource(R.drawable.list_item_background_tinted);
            }
            return rowView;
        }
    }
}

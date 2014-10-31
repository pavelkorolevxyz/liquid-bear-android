package com.pillowapps.liqear.components;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.AlbumViewerActivity;
import com.pillowapps.liqear.activities.ArtistViewerActivity;
import com.pillowapps.liqear.activities.PlaylistsSherlockListActivity;
import com.pillowapps.liqear.activities.TagViewerActivity;
import com.pillowapps.liqear.activities.TrackedActivity;
import com.pillowapps.liqear.audio.AudioTimeline;
import com.pillowapps.liqear.connection.Params;
import com.pillowapps.liqear.connection.ReadyResult;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.helpers.Utils;
import com.pillowapps.liqear.models.Album;
import com.pillowapps.liqear.models.Artist;
import com.pillowapps.liqear.models.ErrorResponseLastfm;
import com.pillowapps.liqear.models.ErrorResponseVk;
import com.pillowapps.liqear.models.MainActivityStartEnum;
import com.pillowapps.liqear.models.Tag;
import com.pillowapps.liqear.models.Track;

import java.util.ArrayList;
import java.util.List;

public class ResultSherlockActivity extends TrackedActivity {
    public static final String TAB_INDEX = "tab_index";
    public int TRACKS_IN_TOP_COUNT = getPageSize();

    private int getPageSize() {
        return PreferencesManager.getPreferences().getInt("page_size", 50);
    }

    protected boolean checkForError(ReadyResult result, Params.ApiSource source) {
        final boolean error = !result.isOk();
        int errorCode = -1;
        if (result.getObject() instanceof ErrorResponseLastfm) {
            errorCode = ((ErrorResponseLastfm) result.getObject()).getError();
        } else if (result.getObject() instanceof ErrorResponseVk) {
            errorCode = ((ErrorResponseVk) result.getObject()).getErrorCode();
        }
        if (error && errorCode != 0) {
            Utils.showErrorDialog(result, ResultSherlockActivity.this, source);
        }
        return error;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constants.MAIN_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void openMainPlaylist(List<Track> tracks, int position) {
        boolean local = false;
        try {
            local = tracks.get(position).isLocal();
        } catch (Exception ignored) {
        }
        openMainPlaylist(tracks, position, local);

    }

    public void openMainPlaylist(List<Track> tracks, int position, boolean local) {
        if (!AuthorizationInfoManager.isAuthorizedOnVk() && !local) {
            Toast.makeText(ResultSherlockActivity.this, R.string.vk_not_authorized,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent data = new Intent();
        data.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        data.putExtra(Constants.ACTION_ENUM, MainActivityStartEnum.PLAY_TRACKS);
        data.putExtra(Constants.POSITION_TO_PLAY, position);
        AudioTimeline.setPlaylist(tracks);
        setResult(RESULT_OK, data);
        finish();
    }

    public void addToMainPlaylist(List<Track> tracks) {
        if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
            Toast.makeText(ResultSherlockActivity.this, R.string.vk_not_authorized,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        AudioTimeline.addToPlaylist(tracks);
    }

    public void trackLongClick(List<Track> tracks, int position) {
        if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
            Toast.makeText(ResultSherlockActivity.this, R.string.vk_not_authorized,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        AudioTimeline.addToPlaylist(tracks.get(position));
    }

    public void saveAsPlaylist(List<Track> tracks) {
        Intent intent = new Intent(ResultSherlockActivity.this,
                PlaylistsSherlockListActivity.class);
        intent.putExtra("aim", PlaylistsSherlockListActivity.Aim.SAVE_AS_PLAYLIST);
        intent.putParcelableArrayListExtra(Constants.TRACKLIST, (ArrayList<Track>) tracks);
        startActivity(intent);
    }

    protected void openArtist(Artist artist) {
        Intent intent = new Intent(ResultSherlockActivity.this, ArtistViewerActivity.class);
        intent.putExtra(ArtistViewerActivity.ARTIST, artist.getName());
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
    }

    protected void openTag(Tag tag) {
        Intent intent = new Intent(ResultSherlockActivity.this, TagViewerActivity.class);
        intent.putExtra(TagViewerActivity.TAG, tag.getName());
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
    }

    protected void openAlbum(Album album) {
        Intent intent = new Intent(ResultSherlockActivity.this, AlbumViewerActivity.class);
        intent.putExtra(AlbumViewerActivity.ARTIST, album.getArtist());
        intent.putExtra(AlbumViewerActivity.ALBUM, album.getTitle());
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
    }

    protected void setOpenMainPlaylist(final ViewerPage<Track> viewer) {
        viewer.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                openMainPlaylist(viewer.getValues(), position);
            }
        });
    }

    protected void setTrackLongClick(final ViewerPage<Track> viewer) {
        viewer.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                trackLongClick(viewer.getValues(), i);
                return true;
            }
        });
    }

    protected void setOpenArtistListener(final ViewerPage<Artist> viewer) {
        viewer.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                openArtist(viewer.get(position));
            }
        });
    }

    protected void setOpenTagListener(final ViewerPage<Tag> viewer) {
        viewer.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                openTag(viewer.get(position));
            }
        });
    }

    protected void setOpenAlbumListener(final ViewerPage<Album> viewer) {
        viewer.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                openAlbum(viewer.get(position));
            }
        });
    }
}

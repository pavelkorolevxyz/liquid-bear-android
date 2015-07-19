package com.pillowapps.liqear.components;

import android.content.Intent;
import android.widget.Toast;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.SearchActivity;
import com.pillowapps.liqear.activities.TrackedActivity;
import com.pillowapps.liqear.activities.viewers.LastfmAlbumViewerActivity;
import com.pillowapps.liqear.activities.viewers.LastfmArtistViewerActivity;
import com.pillowapps.liqear.activities.viewers.LastfmTagViewerActivity;
import com.pillowapps.liqear.activities.viewers.VkUserViewerActivity;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.MainActivityStartEnum;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Tag;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.vk.VkAlbum;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;

import java.util.List;

public class ResultActivity extends TrackedActivity {
    public static final String TAB_INDEX = "tab_index";
    public int TRACKS_IN_TOP_COUNT = getPageSize();

    protected int getPageSize() {
        return SharedPreferencesManager.getPreferences().getInt("page_size", 50);
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
        boolean local = tracks.get(position).isLocal();
        openMainPlaylist(tracks, position, local);

    }

    public void openMainPlaylist(List<Track> tracks, int position, boolean local) {
        if (!AuthorizationInfoManager.isAuthorizedOnVk() && !local) {
            Toast.makeText(ResultActivity.this, R.string.vk_not_authorized,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent data = new Intent();
        data.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        data.putExtra(Constants.ACTION_ENUM, MainActivityStartEnum.PLAY_TRACKS);
        data.putExtra(Constants.POSITION_TO_PLAY, position);
        Timeline.getInstance().setPlaylist(new Playlist(tracks));
        setResult(RESULT_OK, data);
        finish();
    }

    public void addToMainPlaylist(List<Track> tracks) {
        if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
            Toast.makeText(ResultActivity.this, R.string.vk_not_authorized,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Timeline.getInstance().addToPlaylist(tracks);
    }

    public void trackLongClick(List<Track> tracks, int position) {
        if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
            Toast.makeText(ResultActivity.this, R.string.vk_not_authorized,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Timeline.getInstance().addToPlaylist(tracks.get(position));
    }

    public void trackLongClick(Track track) {
        if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
            Toast.makeText(ResultActivity.this, R.string.vk_not_authorized,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Timeline.getInstance().addToPlaylist(track);
    }

    public void saveAsPlaylist(List<Track> tracks) {
//        Intent intent = new Intent(ResultActivity.this,
//                PlaylistsActivity.class);
//        intent.putExtra("aim", PlaylistsActivity.Aim.SAVE_AS_PLAYLIST);
//        intent.putParcelableArrayListExtra(Constants.TRACKLIST, (ArrayList<Track>) tracks);
//        startActivity(intent);
        //todo with otto
    }

    protected void openArtistByName(String name) {
        Intent intent = new Intent(ResultActivity.this, LastfmArtistViewerActivity.class);
        intent.putExtra(LastfmArtistViewerActivity.ARTIST, name);
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
    }

    protected void openTag(Tag tag) {
        Intent intent = new Intent(ResultActivity.this, LastfmTagViewerActivity.class);
        intent.putExtra(LastfmTagViewerActivity.TAG, tag.getName());
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
    }

    protected void openAlbum(Album album) {
        Intent intent = new Intent(ResultActivity.this, LastfmAlbumViewerActivity.class);
        intent.putExtra(LastfmAlbumViewerActivity.ARTIST, album.getArtist());
        intent.putExtra(LastfmAlbumViewerActivity.ALBUM, album.getTitle());
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
    }

    protected void openVkAlbum(VkAlbum vkAlbum) {
        Intent searchIntent = new Intent(ResultActivity.this,
                SearchActivity.class);
        searchIntent.putExtra("title", vkAlbum.getTitle());
        VkUserViewerActivity.Mode mode = VkUserViewerActivity.Mode.USER;
        if (mode == VkUserViewerActivity.Mode.USER) {
            searchIntent.putExtra("uid", vkAlbum.getOwnerId());
        } else {
            searchIntent.putExtra("gid", vkAlbum.getOwnerId());
        }
        searchIntent.putExtra("album_id", vkAlbum.getAlbumId());
        searchIntent.putExtra(SearchActivity.SEARCH_MODE,
                SearchActivity.SearchMode.VK_ALBUM_TRACKLIST);
        startActivityForResult(searchIntent, Constants.MAIN_REQUEST_CODE);
    }

    protected void showError(String errorMessage) {
        ErrorNotifier.showError(this, errorMessage);
    }
}

package com.pillowapps.liqear.activities.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.HomeActivity;
import com.pillowapps.liqear.activities.modes.VkAlbumTracksActivity;
import com.pillowapps.liqear.activities.modes.viewers.LastfmAlbumViewerActivity;
import com.pillowapps.liqear.activities.modes.viewers.LastfmArtistViewerActivity;
import com.pillowapps.liqear.activities.modes.viewers.LastfmTagViewerActivity;
import com.pillowapps.liqear.activities.modes.viewers.LastfmUserViewerActivity;
import com.pillowapps.liqear.activities.modes.viewers.VkUserViewerActivity;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Group;
import com.pillowapps.liqear.entities.MainActivityStartEnum;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Tag;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.entities.vk.VkAlbum;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.helpers.LBPreferencesManager;

import java.util.List;

import javax.inject.Inject;

// todo make simpler
public abstract class ResultTrackedBaseActivity extends TrackedBaseActivity {

    @Inject
    Timeline timeline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LBApplication.get(this).applicationComponent().inject(this);
    }

    protected int getPageSize() {
        return LBPreferencesManager.getPageSize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constants.MAIN_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void openMainPlaylist(List<Track> tracks, int position, CharSequence title) {
        boolean local = tracks.get(position).isLocal();
        openMainPlaylist(tracks, position, title, local);
    }

    public void openMainPlaylist(List<Track> tracks, int position, CharSequence title, boolean local) {
        if (!AuthorizationInfoManager.isAuthorizedOnVk() && !local) {
            Toast.makeText(ResultTrackedBaseActivity.this, R.string.vk_not_authorized,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent data = new Intent();
        data.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        data.putExtra(Constants.ACTION_ENUM, MainActivityStartEnum.PLAY_TRACKS);
        data.putExtra(Constants.POSITION_TO_PLAY, position);
        Playlist playlist = new Playlist(tracks);
        playlist.setTitle(String.valueOf(title));
        timeline.setPlaylist(playlist);
        setResult(RESULT_OK, data);
        finish();
    }

    public void addToMainPlaylist(List<Track> tracks) {
        if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
            Toast.makeText(ResultTrackedBaseActivity.this, R.string.vk_not_authorized,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        timeline.addToPlaylist(tracks);
    }

    public void trackLongClick(List<Track> tracks, int position) {
        if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
            Toast.makeText(ResultTrackedBaseActivity.this, R.string.vk_not_authorized,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        timeline.addToPlaylist(tracks.get(position));
    }

    public void trackLongClick(Track track) {
        if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
            Toast.makeText(ResultTrackedBaseActivity.this, R.string.vk_not_authorized,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        timeline.addToPlaylist(track);

        Toast.makeText(ResultTrackedBaseActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(ResultTrackedBaseActivity.this, LastfmArtistViewerActivity.class);
        intent.putExtra(LastfmArtistViewerActivity.ARTIST, name);
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
    }

    protected void openTag(Tag tag) {
        Intent intent = new Intent(ResultTrackedBaseActivity.this, LastfmTagViewerActivity.class);
        intent.putExtra(LastfmTagViewerActivity.TAG, tag.getName());
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
    }

    protected void openLastfmUser(User user) {
        Intent intent = new Intent(ResultTrackedBaseActivity.this, LastfmUserViewerActivity.class);
        intent.putExtra(LastfmUserViewerActivity.USER, user);
        intent.putExtra(LastfmUserViewerActivity.TAB_INDEX, LastfmUserViewerActivity.LOVED_INDEX);
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
    }

    protected void openGroup(Group group) {
        Intent userViewerIntent = new Intent(ResultTrackedBaseActivity.this, VkUserViewerActivity.class);
        userViewerIntent.putExtra(VkUserViewerActivity.GROUP, group);
        startActivityForResult(userViewerIntent, Constants.MAIN_REQUEST_CODE);
    }

    protected void openVkUser(User user) {
        Intent userViewerIntent = new Intent(ResultTrackedBaseActivity.this, VkUserViewerActivity.class);
        userViewerIntent.putExtra(VkUserViewerActivity.USER, user);
        startActivityForResult(userViewerIntent, Constants.MAIN_REQUEST_CODE);
    }

    protected void openLastfmAlbum(Album album) {
        Intent intent = new Intent(ResultTrackedBaseActivity.this, LastfmAlbumViewerActivity.class);
        intent.putExtra(LastfmAlbumViewerActivity.ARTIST, album.getArtist());
        intent.putExtra(LastfmAlbumViewerActivity.ALBUM, album.getTitle());
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
    }

    protected void openVkAlbum(Album vkAlbum) {
        Intent searchIntent = new Intent(ResultTrackedBaseActivity.this,
                VkAlbumTracksActivity.class);
        searchIntent.putExtra("title", vkAlbum.getTitle());
        searchIntent.putExtra("uid", vkAlbum.getOwnerId());
        searchIntent.putExtra("album_id", vkAlbum.getAlbumId());
        startActivityForResult(searchIntent, Constants.MAIN_REQUEST_CODE);
    }

    protected void openVkAlbum(VkAlbum vkAlbum) {
        Intent searchIntent = new Intent(ResultTrackedBaseActivity.this,
                VkAlbumTracksActivity.class);
        searchIntent.putExtra("title", vkAlbum.getTitle());
        searchIntent.putExtra("uid", vkAlbum.getOwnerId());
        searchIntent.putExtra("album_id", vkAlbum.getAlbumId());
        startActivityForResult(searchIntent, Constants.MAIN_REQUEST_CODE);
    }

    protected void openGroupVkAlbum(Album vkAlbum) {
        Intent searchIntent = new Intent(ResultTrackedBaseActivity.this,
                VkAlbumTracksActivity.class);
        searchIntent.putExtra("title", vkAlbum.getTitle());
        searchIntent.putExtra("gid", vkAlbum.getOwnerId());
        searchIntent.putExtra("album_id", vkAlbum.getAlbumId());
        startActivityForResult(searchIntent, Constants.MAIN_REQUEST_CODE);
    }

    protected void showError(String errorMessage) {
        ErrorNotifier.showError(this, errorMessage);
    }

    protected void showError(VkError error) {
        ErrorNotifier.showError(this, error.getErrorMessage());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                Intent intent = new Intent(ResultTrackedBaseActivity.this, HomeActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            default:
                return true;
        }
        return true;
    }
}

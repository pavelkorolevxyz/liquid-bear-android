package com.pillowapps.liqear.helpers.home;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;

import java.util.LinkedList;

public interface HomeView {

    void showLoading(boolean loading);

    void changeViewPagerItem(int page);

    void updateAdapter();

    void changePlaylist(int index, Playlist playlist, LinkedList<Integer> queueIndexes);

    void playTrack(int index, boolean autoplay);

    void updateEmptyPlaylistTextView();

    void showNoInternetError();

    void showError(String errorMessage);

    void openArtistPhotosScreen(String artist);

    void openArtistViewer(String artist);

    void showShareDialog(String shareMessage, String imageUrl, Track track);

    void showVkAuthorizationError();

    void showTrackIsLocalError();

    void openVkAudioSearchForNextUrl(Track currentTrack);

    void openLyricsScreen(Track track);

    void openTrackVideo(Track track);

    void openAddToVkScreen(Track track);

    void showToastAdded();

    void openPreferences();

    void openEqualizer();

    void showTimerDialog();

    void clearSearch();

    void setMainPlaylistSelection(int currentIndex);

    void updateModeListEditMode();

    void updateSearchVisibility(boolean visibility);

    void exit();

    void openPlaylistsScreen();

    void togglePlaylistEditMode();

    void changeCurrentTrackUrl(int newPosition, String url);

    void updateMainPlaylistTitle(@Nullable String string);

    void showArtistPlaceholder();

    void updateTrackArtist(String artist);

    void updateTrackTitle(String title);

    void updateAlbum();

    void updateShuffleButtonState(int imageRes);

    void updateRepeatButtonState(int imageRes);

    void hideTutorial();

    void showTutorial();

    void updateArtistPhotoAndColors(String artistUrl);

    void hideAlbumImage();

    void showAlbumImage(String imageUrl);

    void hideAlbumTitle();

    void showAlbumTitle(String albumTitle);

    void updateWidgets();

    void setTimer(int minutes);

    void updateLoveButton(int imageRes);

    void openAlbumScreen(@NonNull Album album);

    void updatePlaybackTabMenu(int playbackToolbarMenuRes);

    void updatePlayingState(boolean isPlaying);

    void restoreServiceState();

    void openAddTrackToPlaylistScreen(Track track);

    void showAddToDialog(Track track);
}

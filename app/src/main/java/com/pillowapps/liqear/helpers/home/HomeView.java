package com.pillowapps.liqear.helpers.home;

import com.pillowapps.liqear.entities.Track;

public interface HomeView {

    void showLoading(boolean loading);

    void changeViewPagerItem(int page);

    void updateAdapter();

    void changePlaylist(int index, boolean autoPlay);

    void playTrack(int index);

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

    void changeCurrentTrackUrl(int newPosition);

    void updateMainPlaylistTitle();

    void showArtistPlaceholder();

    void updateTrackArtist(String artist);

    void updateTrackTitle(String title);

    void updateAlbum();

    void updateShuffleButtonState();

    void updateRepeatButtonState();

    void hideTutorial();

    void showTutorial();
}

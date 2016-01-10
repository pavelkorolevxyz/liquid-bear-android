package com.pillowapps.liqear.helpers.home;

public interface HomeView {

    void showLoading(boolean loading);

    void changeViewPagerItem(int page);

    void updateAdapter();

    void changePlaylist(int index, boolean autoPlay);

    void showError(String errorMessage);

    void playTrack(int index);

    void updateEmptyPlaylistTextView();
}

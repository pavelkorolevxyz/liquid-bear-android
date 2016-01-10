package com.pillowapps.liqear.helpers.home;

public abstract class HomePresenter {

    HomeView view;

    public abstract void init();

    public abstract void setMusicServiceConnected();

    public abstract void openRadiomix();

    public abstract void openLibrary();

    public abstract void playTrack(int index);
}

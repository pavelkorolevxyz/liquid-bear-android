package com.pillowapps.liqear.models.lastfm;

import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmAlbumRoot;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

public class LastfmDiscographyModel {

    public void getDiscographyTracks(List<Album> albums, final SimpleCallback<List<LastfmTrack>> callback) {
        if (albums == null || albums.size() == 0) callback.success(new ArrayList<LastfmTrack>(0));

        final LastfmAlbumModel albumModel = new LastfmAlbumModel();
        List<Observable<LastfmAlbumRoot>> observableList = new ArrayList<>(albums.size());
        for (Album album : albums) {
            Observable<LastfmAlbumRoot> albumInfoObservable = albumModel.getAlbumInfo(album);
            observableList.add(albumInfoObservable);
        }
        Observable.zip(observableList, new FuncN<List<LastfmTrack>>() {
            @Override
            public List<LastfmTrack> call(Object... albumRoots) {
                List<LastfmTrack> tracks = new ArrayList<>();
                for (Object arg : albumRoots) {
                    if (arg == null) continue;
                    if (arg instanceof LastfmAlbumRoot) {
                        LastfmAlbumRoot albumRoot = (LastfmAlbumRoot) arg;
                        tracks.addAll(albumRoot.getAlbum().getTracks().getTracks());
                    }
                }
                return tracks;
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<LastfmTrack>>() {
                    @Override
                    public void call(List<LastfmTrack> tracks) {
                        callback.success(tracks);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.failure(throwable.getMessage());
                    }
                });
    }

}

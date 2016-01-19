package com.pillowapps.liqear.models.lastfm;

import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmAlbumRoot;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LastfmDiscographyModel {

    public void getDiscographyTracks(List<Album> albums, final SimpleCallback<List<LastfmTrack>> callback) {
        if (albums == null || albums.size() == 0) {
            callback.success(new ArrayList<>(0));
            return;
        }

        final LastfmAlbumModel albumModel = new LastfmAlbumModel();
        List<Observable<LastfmAlbumRoot>> observableList = new ArrayList<>(albums.size());
        for (Album album : albums) {
            Observable<LastfmAlbumRoot> albumInfoObservable = albumModel.getAlbumInfo(album);
            observableList.add(albumInfoObservable);
        }
        Observable.zip(observableList, albumRoots -> {
            List<LastfmTrack> tracks = new ArrayList<>();
            for (Object arg : albumRoots) {
                if (arg == null) continue;
                if (arg instanceof LastfmAlbumRoot) {
                    LastfmAlbumRoot albumRoot = (LastfmAlbumRoot) arg;
                    tracks.addAll(albumRoot.getAlbum().getTracks().getTracks());
                }
            }
            return tracks;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::success, throwable -> {
                    callback.failure(throwable.getMessage());
                });
    }

}

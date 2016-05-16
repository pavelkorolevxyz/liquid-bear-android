package com.pillowapps.liqear.models.portals;

import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.network.alterportal.AlterportalReader;

import java.util.List;

import rx.Observable;

public class AlterportalAlbumModel {

    @SuppressWarnings("unchecked")
    public Observable<List<Album>> getNewcomers(final List<Integer> pages) {
        return Observable.defer(() -> Observable.just(new AlterportalReader().selectAlbumsFromPages(pages)));
    }

}

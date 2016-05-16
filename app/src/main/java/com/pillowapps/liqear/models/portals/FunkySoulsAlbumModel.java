package com.pillowapps.liqear.models.portals;

import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.network.funkysouls.FunkySoulsReader;

import java.util.List;

import rx.Observable;

public class FunkySoulsAlbumModel {

    @SuppressWarnings("unchecked")
    public Observable<List<Album>> getNewcomers(final List<Integer> pages) {
        return Observable.defer(() -> Observable.just(new FunkySoulsReader().selectAlbumsFromPages(pages)));
    }

}

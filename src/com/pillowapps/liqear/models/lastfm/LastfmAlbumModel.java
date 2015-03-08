package com.pillowapps.liqear.models.lastfm;

import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmAlbumRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmAlbumSearchResultsRoot;
import com.pillowapps.liqear.network.callbacks.LastfmCallback;
import com.pillowapps.liqear.network.callbacks.LastfmSimpleCallback;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.service.LastfmApiService;

import java.util.List;

public class LastfmAlbumModel {
    private LastfmApiService lastfmService = ServiceHelper.getLastfmService();

    public void getAlbumInfo(Album album, final LastfmSimpleCallback<LastfmAlbum> callback) {
        lastfmService.getAlbumInfo(
                album.getArtist(),
                album.getTitle(),
                new LastfmCallback<LastfmAlbumRoot>() {
                    @Override
                    public void success(LastfmAlbumRoot lastfmAlbumRoot) {
                        callback.success(lastfmAlbumRoot.getAlbum());
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void searchAlbum(String query, int limit, int page,
                            final LastfmSimpleCallback<List<LastfmAlbum>> callback) {
        lastfmService.searchAlbum(query, limit, page, new LastfmCallback<LastfmAlbumSearchResultsRoot>() {
            @Override
            public void success(LastfmAlbumSearchResultsRoot root) {
                callback.success(root.getResults().getAlbums().getAlbums());
            }

            @Override
            public void failure(String error) {
                callback.failure(error);
            }
        });
    }
}

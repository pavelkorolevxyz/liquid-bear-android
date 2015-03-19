package com.pillowapps.liqear.models.lastfm;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.audio.AudioTimeline;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmAlbumRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmAlbumSearchResultsRoot;
import com.pillowapps.liqear.network.callbacks.CompletionCallback;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.callbacks.LastfmCallback;
import com.pillowapps.liqear.network.callbacks.SimpleCallback;
import com.pillowapps.liqear.network.service.LastfmApiService;

import java.util.List;

import rx.Observable;

public class LastfmAlbumModel {
    private LastfmApiService lastfmService = ServiceHelper.getLastfmService();

    public void getAlbumInfo(Album album, final SimpleCallback<LastfmAlbum> callback) {
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

    public Observable<LastfmAlbumRoot> getAlbumInfo(Album album) {
        return lastfmService.getAlbumInfo(
                album.getArtist(),
                album.getTitle()
        );
    }

    public void searchAlbum(String query, int limit, int page,
                            final SimpleCallback<List<LastfmAlbum>> callback) {
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

    public void getCover(final Album album, final CompletionCallback callback) {
        if (album == null) {
            AudioTimeline.setCurrentAlbumBitmap(null);
            callback.onCompleted();
            return;
        }
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(LBApplication.getAppContext(),
                album.getImageUrl(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted() {

                    }

                    @Override
                    public void onLoadingFailed(FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(Bitmap bitmap) {
                        AudioTimeline.setCurrentAlbumBitmap(bitmap);
                        callback.onCompleted();
                    }

                    @Override
                    public void onLoadingCancelled() {

                    }
                });
    }

}

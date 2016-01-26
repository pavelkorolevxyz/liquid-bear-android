package com.pillowapps.liqear.models.lastfm;

import com.pillowapps.liqear.callbacks.retrofit.LastfmCallback;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.lastfm.LastfmTag;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTagSearchResultsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopTracksRoot;
import com.pillowapps.liqear.network.service.LastfmApiService;

import java.util.List;

public class LastfmTagModel {
    private LastfmApiService lastfmService;

    public LastfmTagModel(LastfmApiService api) {
        this.lastfmService = api;
    }

    public void getTagTopTracks(String tag, int limit, int page,
                                final SimpleCallback<List<LastfmTrack>> callback) {
        lastfmService.getTagTopTracks(tag, limit, page,
                new LastfmCallback<LastfmTopTracksRoot>() {
                    @Override
                    public void success(LastfmTopTracksRoot data) {
                        callback.success(data.getTracks().getTracks());
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                });
    }

    public void searchTag(String query, int limit, int page,
                          final SimpleCallback<List<LastfmTag>> callback) {
        lastfmService.searchTag(query, limit, page, new LastfmCallback<LastfmTagSearchResultsRoot>() {
            @Override
            public void success(LastfmTagSearchResultsRoot data) {
                callback.success(data.getResults().getTags().getTags());
            }

            @Override
            public void failure(String error) {
                callback.failure(error);
            }
        });
    }
}

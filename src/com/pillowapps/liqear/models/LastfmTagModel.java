package com.pillowapps.liqear.models;

import com.pillowapps.liqear.entities.lastfm.LastfmTag;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTagSearchResultsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopTracksRoot;
import com.pillowapps.liqear.network.LastfmCallback;
import com.pillowapps.liqear.network.LastfmSimpleCallback;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.service.LastfmApiService;

import java.util.List;

public class LastfmTagModel {
    private LastfmApiService lastfmService = ServiceHelper.getLastfmService();

    public void getTagTopTracks(String tag, int limit, int page,
                                final LastfmSimpleCallback<List<LastfmTrack>> callback) {
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
                          final LastfmSimpleCallback<List<LastfmTag>> callback) {
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

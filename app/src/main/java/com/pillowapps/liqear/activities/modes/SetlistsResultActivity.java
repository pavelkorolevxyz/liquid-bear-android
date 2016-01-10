package com.pillowapps.liqear.activities.modes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.ListBaseActivity;
import com.pillowapps.liqear.adapters.recyclers.SetlistAdapter;
import com.pillowapps.liqear.callbacks.SetlistfmSimpleCallback;
import com.pillowapps.liqear.components.OnRecyclerItemClickListener;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSetlist;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.SetlistfmUtils;
import com.pillowapps.liqear.models.setlistsfm.SetlistsfmSetlistModel;

import java.util.List;

public class SetlistsResultActivity extends ListBaseActivity {

    private SetlistAdapter adapter;
    private String artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setTitle(R.string.setlist);

        Bundle extras = getIntent().getExtras();

        artist = extras.getString("artist");
        String venue = extras.getString("venue");
        String city = extras.getString("city");
        searchSetlists(artist, venue, city);
    }

    private void fillWithSetlists(List<SetlistfmSetlist> setlists) {
        if (adapter == null || adapter.getItemCount() == 0) {
            emptyTextView.setVisibility(setlists.size() == 0 ? View.VISIBLE : View.GONE);
            adapter = new SetlistAdapter(setlists, new OnRecyclerItemClickListener() {
                @Override
                public void onItemClicked(View view, int position) {
                    Intent searchIntent = new Intent(SetlistsResultActivity.this,
                            SetlistTracksActivity.class);
                    SetlistfmSetlist setlist = adapter.getItems().get(position);
                    searchIntent.putStringArrayListExtra("tracks",
                            SetlistfmUtils.getStringTracks(setlist));
                    searchIntent.putExtra("artist", setlist.getArtist().getName());
                    searchIntent.putExtra("notation", setlist.getNotation());
                    startActivityForResult(searchIntent, Constants.MAIN_REQUEST_CODE);
                }
            });
            recycler.setAdapter(adapter);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void searchSetlists(String artist, String venue, final String city) {
        new SetlistsfmSetlistModel().getSetlists(artist, venue, city, new SetlistfmSimpleCallback<List<SetlistfmSetlist>>() {
            @Override
            public void success(List<SetlistfmSetlist> setlists) {
                fillWithSetlists(setlists);
            }

            @Override
            public void failure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}

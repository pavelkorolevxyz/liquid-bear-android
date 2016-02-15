package com.pillowapps.liqear.activities.modes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.ImagePagerActivity;
import com.pillowapps.liqear.activities.base.ListBaseActivity;
import com.pillowapps.liqear.adapters.recyclers.SetlistAdapter;
import com.pillowapps.liqear.callbacks.SetlistfmSimpleCallback;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSetlist;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.SetlistfmUtils;
import com.pillowapps.liqear.models.setlistsfm.SetlistsfmSetlistModel;

import java.util.List;

import javax.inject.Inject;

public class SetlistsResultActivity extends ListBaseActivity {

    private SetlistAdapter adapter;

    @Inject
    SetlistsfmSetlistModel setlistfmSetlistModel;

    public static Intent startIntent(Context context) {
        return new Intent(context, SetlistsResultActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LBApplication.get(this).applicationComponent().inject(this);

        setTitle(R.string.setlist);

        Bundle extras = getIntent().getExtras();

        String artist = extras.getString("artist");
        String venue = extras.getString("venue");
        String city = extras.getString("city");
        searchSetlists(artist, venue, city);
    }

    private void fillWithSetlists(List<SetlistfmSetlist> setlists) {
        if (adapter == null || adapter.getItemCount() == 0) {
            adapter = new SetlistAdapter(setlists, (view, position) -> {
                Intent searchIntent = new Intent(SetlistsResultActivity.this,
                        SetlistTracksActivity.class);
                SetlistfmSetlist setlist = adapter.getItems().get(position);
                searchIntent.putStringArrayListExtra("tracks",
                        SetlistfmUtils.getStringTracks(setlist));
                searchIntent.putExtra("artist", setlist.getArtist().getName());
                searchIntent.putExtra("notation", setlist.getNotation());
                startActivityForResult(searchIntent, Constants.MAIN_REQUEST_CODE);
            });
            recycler.setAdapter(adapter);
        }
        progressBar.setVisibility(View.GONE);
        updateEmptyTextView();
    }

    private void searchSetlists(String artist, String venue, final String city) {
        setlistfmSetlistModel.getSetlists(artist, venue, city, new SetlistfmSimpleCallback<List<SetlistfmSetlist>>() {
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

package com.pillowapps.liqear.activities.modes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.adapters.TagAdapter;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.Tag;
import com.pillowapps.liqear.entities.lastfm.LastfmTag;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.DelayedTextWatcher;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.models.lastfm.LastfmTagModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class SearchTagActivity extends SearchBaseActivity {

    private TagAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setTitle(getString(R.string.tag_radio));
        editText.setHint(getString(R.string.tag_radio));
        editText.setFloatingLabelText(getString(R.string.tag_radio));
        progressBar.setVisibility(View.GONE);
        loadTagPresets();

        initWatcher();
    }

    protected void initWatcher() {
        editText.addTextChangedListener(new DelayedTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                runTaskWithDelay(new Runnable() {
                    @Override
                    public void run() {
                        String searchQuery = editText.getText().toString().trim();
                        if (searchQuery.length() == 0) {
                            loadTagPresets();
                            return;
                        }
                        searchTag(searchQuery, getPageSize(), 0);
                    }
                });
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadTagPresets() {
        LinkedHashSet<Tag> tags = new LinkedHashSet<>(Constants.PRESET_WANTED_COUNT);
        SharedPreferences tagPreferences = SharedPreferencesManager.getTagPreferences();
        int tagsCount = tagPreferences.getInt(Constants.PRESET_LAST_NUMBER, 0);
        if (tagsCount >= Constants.PRESET_WANTED_COUNT) {
            for (int i = tagsCount - 1; i >= tagsCount - Constants.PRESET_WANTED_COUNT; i--) {
                tags.add(new Tag(tagPreferences.getString(Constants.TAG_NUMBER
                        + (i % Constants.PRESET_WANTED_COUNT), "")));
            }
        } else {
            for (int i = tagsCount - 1; i >= 0; i--) {
                tags.add(new Tag(tagPreferences.getString(Constants.TAG_NUMBER + i, "")));
            }
        }
        String[] presets = getResources().getStringArray(R.array.tag_presets);
        int i = 0;
        while (tags.size() < Constants.PRESET_WANTED_COUNT) {
            tags.add(new Tag(presets[i++]));
        }
        fillWithTags(new ArrayList<>(tags));
    }

    private void fillWithTags(List<Tag> tags) {
        emptyTextView.setVisibility(tags.size() == 0 ? View.VISIBLE : View.GONE);
        adapter = new TagAdapter(tags, new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                openTag(adapter.getItem(position));
            }
        });
        recycler.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }


    private void searchTag(String searchQuery, int limit, int page) {
        new LastfmTagModel().searchTag(searchQuery, limit, page,
                new SimpleCallback<List<LastfmTag>>() {
                    @Override
                    public void success(List<LastfmTag> tags) {
                        fillWithTags(Converter.convertTags(tags));
                    }

                    @Override
                    public void failure(String error) {
                        showError(error);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}

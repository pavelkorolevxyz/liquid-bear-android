package com.pillowapps.liqear.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.audio.AudioTimeline;
import com.pillowapps.liqear.audio.MusicPlaybackService;
import com.pillowapps.liqear.components.ResultActivity;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkLyrics;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.models.lastfm.LastfmArtistModel;
import com.pillowapps.liqear.models.vk.VkLyricsModel;
import com.pillowapps.liqear.network.callbacks.SimpleCallback;
import com.pillowapps.liqear.network.callbacks.VkSimpleCallback;

public class TextActivity extends ResultActivity {
    public static final String ARTIST_NAME = "artist_name";
    public static final String TEXT_AIM = "text_aim";
    private TextView textView;
    private ProgressBar progressBar;
    private Aim aim;
    private String googleRequest;
    private int lyricsNumber;
    private ServiceBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrollable_text_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        textView = (TextView) findViewById(R.id.text_view_scrollable_text_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_scrallable_text_layout);
        Bundle extras = getIntent().getExtras();
        aim = (Aim) extras.get(TEXT_AIM);
        switch (aim) {
            case ARTIST_INFO:
                googleRequest = extras.getString(ARTIST_NAME);
                actionBar.setTitle(Html.fromHtml(googleRequest));
                getArtistInfo(googleRequest, AuthorizationInfoManager.getLastfmName());
                progressBar.setVisibility(View.VISIBLE);
                break;
            case LYRICS:
                String artist = extras.getString("artist");
                String title = extras.getString("title");
                googleRequest = artist + " - " + title;
                actionBar.setTitle(Html.fromHtml(googleRequest));
                lyricsNumber = PreferencesManager.getLyricsNumberPreferences()
                        .getInt(googleRequest, 0);
                getTrackLyrics(new Track(artist, title), lyricsNumber);
                progressBar.setVisibility(View.VISIBLE);

                receiver = new ServiceBroadcastReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Constants.ACTION_SERVICE);
                registerReceiver(receiver, intentFilter);

                break;
            case DISCLAIMER:
                actionBar.setTitle(getResources().getString(R.string.disclaimer));
                textView.setText(getResources().getString(R.string.disclaimer_text));
                break;
            case THANKS:
                actionBar.setTitle(getResources().getString(R.string.thanks_to));
                textView.setText(getResources().getString(R.string.thanks_text));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (Aim.LYRICS == aim) {
            switch (itemId) {
                case android.R.id.home:
                    finish();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                case R.id.share:
                    shareText();
                    break;
                case R.id.google:
                    String url;
                    if (PreferencesManager.getPreferences()
                            .getBoolean("lucky_search_check_box_preferences", true)) {
                        url = "http://www.google.com/webhp#q="
                                + Uri.encode(googleRequest + " lyrics") + "&btnI";
                    } else {
                        url = "http://google.com/search?&sourceid=navclient&q="
                                + Uri.encode(googleRequest + " lyrics");
                    }
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    break;
                case R.id.next_result:
                    lyricsNumber++;
                    final SharedPreferences lyricsNumberPreferences =
                            PreferencesManager.getLyricsNumberPreferences();
                    final SharedPreferences.Editor editor = lyricsNumberPreferences.edit();
                    editor.putInt(googleRequest,
                            lyricsNumberPreferences.getInt(googleRequest, 0) + 1);
                    editor.apply();
                    getTrackLyrics(googleRequest, lyricsNumber);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
            }
            return true;
        } else {
            switch (itemId) {
                case android.R.id.home:
                    finish();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                case 0:
                    if (Aim.ARTIST_INFO == aim) {
                        String url = "http://google.com/search?&sourceid=navclient&q="
                                + Uri.encode(googleRequest + " band");
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                    break;
            }
        }
        return true;
    }

    private void shareText() {
        String shareBody = textView.getText().toString() + "\n\n" + googleRequest + " #LiquidBear";
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_track)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (aim) {
            case LYRICS:
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.lyrics_menu, menu);
                break;
            case ARTIST_INFO:
                MenuItem item = menu.add(getResources().getString(R.string.search_google));
                item.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_search, null));
                break;
            default:
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void getArtistInfo(String artist, String username) {
        new LastfmArtistModel().getArtistInfo(artist, username, new SimpleCallback<LastfmArtist>() {
            @Override
            public void success(LastfmArtist lastfmArtist) {
                progressBar.setVisibility(View.GONE);
                String info = lastfmArtist.getBio().getContent();
                if (info.length() == 0) {
                    info = getString(R.string.not_found);
                }
                textView.setText(Html.fromHtml(info.replace("\n", "<br />").trim()));
            }

            @Override
            public void failure(String error) {
                progressBar.setVisibility(View.GONE);
                ErrorNotifier.showError(TextActivity.this, error);
            }
        });
    }

    private void getTrackLyrics(Track track, int index) {
        new VkLyricsModel().getLyrics(track, index, new VkSimpleCallback<VkLyrics>() {
            @Override
            public void success(VkLyrics lyrics) {
                textView.scrollTo(0, 0);
                String lyricsText = lyrics.getText();
                if (lyricsText.isEmpty()) {
                    lyricsText = getString(R.string.not_found);
                }
                textView.setText(Html.fromHtml(lyricsText.replace("\n", "<br />")));
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(VkError error) {

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getTrackLyrics(String notation, int lyricsNumber) {
        new VkLyricsModel().getLyrics(notation, lyricsNumber, new VkSimpleCallback<VkLyrics>() {
            @Override
            public void success(VkLyrics lyrics) {
                String lyricsText = lyrics == null
                        ? getString(R.string.not_found)
                        : lyrics.getText();
                textView.setText(Html.fromHtml(lyricsText.replace("\n", "<br />")));
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(VkError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public enum Aim {
        ARTIST_INFO,
        LYRICS,
        DISCLAIMER,
        THANKS
    }

    private class ServiceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int callbackType = intent.getIntExtra("callback-type", -1);
            switch (callbackType) {
                case MusicPlaybackService.TRACK_INFO_CALLBACK:
                    Track track = AudioTimeline.getCurrentTrack();
                    googleRequest = track.getNotation();
                    getSupportActionBar().setTitle(googleRequest);
                    getTrackLyrics(track, PreferencesManager
                            .getLyricsNumberPreferences().getInt(googleRequest, 0));
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }

    }
}

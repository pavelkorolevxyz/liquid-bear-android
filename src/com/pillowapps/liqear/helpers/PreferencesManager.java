package com.pillowapps.liqear.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.pillowapps.liqear.LiqearApplication;

public class PreferencesManager {
    private static final String LASTFM_PREFERENCES = "lastfm_preferences";
    private static final String SAVE_PREFERENCES = "save_preferences";
    private static final String ALBUM_PREFERENCES = "album_preferences";
    private static final String TAG_PREFERENCES = "tag_preferences";
    private static final String ARTIST_PREFERENCES = "artist_preferences";
    private static final String URL_PREFERENCES = "url_preferences";
    private static final String EQUALIZER_PREFERENCES = "equalizer_preferences";
    private static final String DATABASE_PREFERENCES = "database_preferences";
    private static final String LYRICS_NUMBER_PREFERENCES = "lyrics_number_preferences";
    private static final String MODE_PREFERENCES = "mode_preferences";
    private static final String START_PREFERENCES = "start_preferences";
    private static SharedPreferences lastfmPreferences;
    private static SharedPreferences preferences;
    private static SharedPreferences savePreferences;
    private static SharedPreferences albumPreferences;
    private static SharedPreferences tagPreferences;
    private static SharedPreferences artistPreferences;
    private static SharedPreferences urlNumberPreferences;
    private static SharedPreferences equalizerPreferences;
    private static SharedPreferences databasePreferences;
    private static SharedPreferences lyricsNumberPreferences;
    private static SharedPreferences modePreferences;
    private static SharedPreferences startPreferences;

    public static SharedPreferences getLastfmPreferences(Context context) {
        if (lastfmPreferences == null) {
            lastfmPreferences = context.getSharedPreferences(LASTFM_PREFERENCES,
                    Context.MODE_PRIVATE);
        }
        return lastfmPreferences;
    }

    public static SharedPreferences getPreferences() {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(
                    LiqearApplication.getAppContext());
        }
        return preferences;
    }

    public static SharedPreferences getSavePreferences() {
        if (savePreferences == null) {
            savePreferences = LiqearApplication.getAppContext()
                    .getSharedPreferences(SAVE_PREFERENCES, Context.MODE_PRIVATE);
        }
        return savePreferences;
    }

    public static SharedPreferences getAlbumPreferences() {
        if (albumPreferences == null) {
            albumPreferences = LiqearApplication.getAppContext()
                    .getSharedPreferences(ALBUM_PREFERENCES, Context.MODE_PRIVATE);
        }
        return albumPreferences;
    }

    public static SharedPreferences getTagPreferences() {
        if (tagPreferences == null) {
            tagPreferences = LiqearApplication.getAppContext()
                    .getSharedPreferences(TAG_PREFERENCES, Context.MODE_PRIVATE);
        }
        return tagPreferences;
    }

    public static SharedPreferences getArtistPreferences() {
        if (artistPreferences == null) {
            artistPreferences = LiqearApplication.getAppContext()
                    .getSharedPreferences(ARTIST_PREFERENCES, Context.MODE_PRIVATE);
        }
        return artistPreferences;
    }

    public static SharedPreferences getUrlNumberPreferences() {
        if (urlNumberPreferences == null) {
            urlNumberPreferences = LiqearApplication.getAppContext()
                    .getSharedPreferences(URL_PREFERENCES, Context.MODE_PRIVATE);
        }
        return urlNumberPreferences;
    }

    public static SharedPreferences getEqualizerPreferences() {
        if (equalizerPreferences == null) {
            equalizerPreferences = LiqearApplication.getAppContext()
                    .getSharedPreferences(EQUALIZER_PREFERENCES, Context.MODE_PRIVATE);
        }
        return equalizerPreferences;
    }

    public static SharedPreferences getDatabasePreferences() {
        if (databasePreferences == null) {
            databasePreferences = LiqearApplication.getAppContext()
                    .getSharedPreferences(DATABASE_PREFERENCES, Context.MODE_PRIVATE);
        }
        return databasePreferences;
    }

    public static SharedPreferences getLyricsNumberPreferences() {
        if (lyricsNumberPreferences == null) {
            lyricsNumberPreferences = LiqearApplication.getAppContext()
                    .getSharedPreferences(LYRICS_NUMBER_PREFERENCES, Context.MODE_PRIVATE);
        }
        return lyricsNumberPreferences;
    }

    public static SharedPreferences getModePreferences() {
        if (modePreferences == null) {
            modePreferences = LiqearApplication.getAppContext()
                    .getSharedPreferences(MODE_PREFERENCES, Context.MODE_PRIVATE);
        }
        return modePreferences;
    }
    public static SharedPreferences getStartPreferences() {
        if (startPreferences == null) {
            startPreferences = LiqearApplication.getAppContext()
                    .getSharedPreferences(START_PREFERENCES, Context.MODE_PRIVATE);
        }
        return startPreferences;
    }
}

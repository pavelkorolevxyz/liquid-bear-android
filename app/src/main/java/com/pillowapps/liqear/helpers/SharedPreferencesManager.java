package com.pillowapps.liqear.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pillowapps.liqear.LBApplication;

// todo remove / refactor
@Deprecated
public class SharedPreferencesManager {
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

    private SharedPreferencesManager() {
        // no-op
    }

    public static SharedPreferences getLastfmPreferences(Context context) {
        if (lastfmPreferences == null) {
            lastfmPreferences = context.getSharedPreferences(LASTFM_PREFERENCES,
                    Context.MODE_PRIVATE);
        }
        return lastfmPreferences;
    }

    public static SharedPreferences getSavePreferences(Context context) {
        if (savePreferences == null) {
            savePreferences = context
                    .getSharedPreferences(SAVE_PREFERENCES, Context.MODE_PRIVATE);
        }
        return savePreferences;
    }

    public static SharedPreferences getAlbumPreferences(Context context) {
        if (albumPreferences == null) {
            albumPreferences = context
                    .getSharedPreferences(ALBUM_PREFERENCES, Context.MODE_PRIVATE);
        }
        return albumPreferences;
    }

    public static SharedPreferences getTagPreferences(Context context) {
        if (tagPreferences == null) {
            tagPreferences = context
                    .getSharedPreferences(TAG_PREFERENCES, Context.MODE_PRIVATE);
        }
        return tagPreferences;
    }

    public static SharedPreferences getArtistPreferences(Context context) {
        if (artistPreferences == null) {
            artistPreferences = context
                    .getSharedPreferences(ARTIST_PREFERENCES, Context.MODE_PRIVATE);
        }
        return artistPreferences;
    }

    public static SharedPreferences getUrlNumberPreferences(Context context) {
        if (urlNumberPreferences == null) {
            urlNumberPreferences = context
                    .getSharedPreferences(URL_PREFERENCES, Context.MODE_PRIVATE);
        }
        return urlNumberPreferences;
    }

    public static SharedPreferences getEqualizerPreferences(Context context) {
        if (equalizerPreferences == null) {
            equalizerPreferences = context
                    .getSharedPreferences(EQUALIZER_PREFERENCES, Context.MODE_PRIVATE);
        }
        return equalizerPreferences;
    }

    public static SharedPreferences getDatabasePreferences(Context context) {
        if (databasePreferences == null) {
            databasePreferences = context
                    .getSharedPreferences(DATABASE_PREFERENCES, Context.MODE_PRIVATE);
        }
        return databasePreferences;
    }

    public static SharedPreferences getLyricsNumberPreferences(Context context) {
        if (lyricsNumberPreferences == null) {
            lyricsNumberPreferences = context
                    .getSharedPreferences(LYRICS_NUMBER_PREFERENCES, Context.MODE_PRIVATE);
        }
        return lyricsNumberPreferences;
    }

    public static SharedPreferences getModePreferences(Context context) {
        if (modePreferences == null) {
            modePreferences = context
                    .getSharedPreferences(MODE_PREFERENCES, Context.MODE_PRIVATE);
        }
        return modePreferences;
    }

    public static SharedPreferences getStartPreferences(Context context) {
        if (startPreferences == null) {
            startPreferences = context
                    .getSharedPreferences(START_PREFERENCES, Context.MODE_PRIVATE);
        }
        return startPreferences;
    }
}

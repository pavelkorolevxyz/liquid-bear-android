package com.pillowapps.liqear.network;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.audio.AudioTimeline;
import com.pillowapps.liqear.components.CancellableThread;
import com.pillowapps.liqear.network.alterportal.AlterportalReader;
import com.pillowapps.liqear.network.funkysouls.FunkySoulsReader;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PlaylistManager;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.helpers.StringUtils;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.TrackUrlQuery;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Entry point into the API.
 */
@SuppressWarnings({"unchecked"})
public class QueryManager {
    public static final String EXECUTE_LYRICS = "execute.getLyrics";
    public static final String EXECUTE_URL = "execute.u";
    public static final String EXECUTE_SEARCH_AND_POST_STATUS = "execute.saps";
    public static final String FRIENDS_GET = "friends.get";
    public static final String GROUPS_GET = "groups.get";
    public static final String STATUS_SET = "status.set";
    public static final String AUDIO_ADD = "audio.add";
    public static final String FAST_AUDIO_ADD = "execute.ta";
    public static final String AUDIO_DELETE = "audio.delete";
    public static final String USERS_GET = "users.get";
    public static final String ALBUM_SEARCH = "album.search";
    public static final String TAG_SEARCH = "tag.search";
    public static final String ARTIST_SEARCH = "artist.search";
    public static final String ARTIST_GET_SIMILAR = "artist.getSimilar";
    public static final String ARTIST_GET_INFO = "artist.getInfo";
    public static final String AUDIO_GET = "audio.get";
    public static final String TRACK_UPDATE_NOW_PLAYING = "track.updateNowPlaying";
    public static final String TRACK_LOVE = "track.love";
    public static final String USER_GET_WEEKLY_TRACK_CHART = "user.getWeeklyTrackChart";
    public static final String USER_GET_LOVED_TRACKS = "user.getLovedTracks";
    public static final String CHART_GET_HYPED_TRACKS = "chart.getHypedTracks";
    public static final String CHART_GET_HYPED_ARTISTS = "chart.getHypedArtists";
    public static final String CHART_GET_LOVED_TRACKS = "chart.getLovedTracks";
    public static final String CHART_GET_TOP_ARTISTS = "chart.getTopArtists";
    public static final String USER_GET_TOP_TRACKS = "user.getTracks";
    public static final String LIBRARY_GET_TRACKS = "library.getTracks";
    public static final String PHOTOS_GET_WALL_UPLOAD_SERVER = "photos.getWallUploadServer";
    public static final String TRACK_GET_INFO = "track.getInfo";
    public static final String ALBUM_GET_INFO = "album.getInfo";
    public static final String USER_GET_INFO = "user.getInfo";
    public static final String ARTIST_GET_TOP_TRACKS = "artist.getTracks";
    public static final String USER_GET_PLAYLISTS = "user.getPlaylists";
    public static final String USER_GET_FRIENDS = "user.getFriends";
    public static final String USER_GET_RECOMMENDED_ARTISTS = "user.getRecommendedArtists";
    public static final String AUDIO_SEARCH = "audio.search";
    public static final String AUDIO_GET_ALBUMS = "audio.getAlbums";
    public static final String LIBRARY = "library";
    public static final String SETLISTS = "setlists";
    public static final String FUNKY = "funky";
    public static final String POST_HARDCORE_RU = "post_hardcore_ru";
    private static final String EXECUTE_LIVE_URL = "execute.getAudioLiveUrl";
    private static final String RECOMMENDATIONS = "recommended";
    private static final String WALL_GET = "wall.get";
    private static final String WALL_POST = "wall.post";
    private static final String AUDIO_GET_RECOMMENDATIONS = "audio.getRecommendations";
    private static final String FAVE_GET_POSTS = "fave.getPosts";
    private static final String ARTIST_GET_TAGS = "artist.getTags";
    private static final String ALTERPORTAL = "alterportal";
    private static final String ARTIST_GET_ALBUMS = "artist.getTopAlbums";
    private static final String USER_GET_TOP_ARTISTS = "user.getTopArtists";
    private static final String PHOTOS_SAVE_WALL_PHOTO = "photos.saveWallPhoto";
    private static final String NEWS_FEED_POST = "newsfeed.get";
    private static TrackUrlQuery trackUrlQuery;
    private final String secret;
    private final String apiKey;
    private final String sk;
    private DefaultHttpClient mHttpClient;
    private GetTask task;
    private CancellableThread scrobbleThread;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public QueryManager() {
        this.apiKey = AuthorizationInfoManager.getLastfmApiKey();
        this.sk = AuthorizationInfoManager.getLastfmKey();
        this.secret = AuthorizationInfoManager.getLastfmSecret();
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        mHttpClient = new DefaultHttpClient(params);
    }

    public static QueryManager getInstance() {
        return new QueryManager();
    }

    private static String createSignature(String method, Map<String,
            String> params, String secret) {
        params = new TreeMap<>(params);
        params.put("method", method);
        StringBuilder b = new StringBuilder(100);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            b.append(entry.getKey());
            b.append(entry.getValue());
        }
        b.append(secret);
        return StringUtils.md5(b.toString());
    }

    public static TrackUrlQuery getTrackUrlQuery() {
        return trackUrlQuery;
    }

    public static void setTrackUrlQuery(TrackUrlQuery trackUrlQuery) {
        QueryManager.trackUrlQuery = trackUrlQuery;
    }

    private void doQuery(final GetResponseCallback callback, Params methodParams) {
        if (task != null) task.cancel(true);
        RestTaskCallback restTaskCallback = new RestTaskCallback() {
            @Override
            public void onTaskComplete(ReadyResult response) {
                if (response != null)
                    callback.onDataReceived(response);
            }
        };
        task = new GetTask(restTaskCallback);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, methodParams);
        } else {
            task.execute(methodParams);
        }
    }

    private void doQuerySync(final GetResponseCallback callback, Params methodParams) {
        RestTaskCallback restTaskCallback = new RestTaskCallback() {
            @Override
            public void onTaskComplete(ReadyResult response) {
                if (response != null)
                    callback.onDataReceived(response);
            }
        };
        task = new GetTask(restTaskCallback);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, methodParams);
        } else {
            task.execute(methodParams);
        }
    }

    public void getNewcomersFunky(final List<Integer> pages,
                                  final GetResponseCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Album> result = new FunkySoulsReader().selectAlbumsFromPages(pages);
                callback.onDataReceived(new ReadyResult(FUNKY, result));
            }
        }).start();
    }

    public void getNewcomersAlterportal(final List<Integer> pages, final GetResponseCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Album> result = new AlterportalReader().selectAlbumsFromPages(pages);
                callback.onDataReceived(new ReadyResult(ALTERPORTAL, result));
            }
        }).start();
    }

    public void getSetlists(String artist, String venue, String city,
                            final GetResponseCallback callback) {
        String url = String.format("http://api.setlist.fm/rest/0.1/search/setlists.json?cityName=%s&venueName=%s&artistName=%s",
                StringUtils.encode(city), StringUtils.encode(venue), StringUtils.encode(artist));
        final Params params = new Params(SETLISTS, ApiMethod.SETLISTS, url);
        params.setApiSource(Params.ApiSource.SETLISTFM);
        doQuery(callback, params);
    }

    public void getRecommendedArtists(int limit, int page, final GetResponseCallback callback) {
        final Map<String, String> params = StringUtils.map("api_key", apiKey, "sk", sk);
        String method = USER_GET_RECOMMENDED_ARTISTS;
        params.put("limit", Integer.toString(limit));
        params.put("page", Integer.toString(page));
        String apiSig = createSignature(method, params, secret);
        params.put("api_sig", apiSig);
        final Params methodParams = new Params(method,
                ApiMethod.USER_GET_RECOMMENDED_ARTISTS, params);
        methodParams.setApiSource(Params.ApiSource.LASTFM);
        doQuery(callback, methodParams);
    }

    public void getArtistTopTracksSync(Artist artist, int limit, int page,
                                       final GetResponseCallback callback) {
        getArtistTopTracksSync(artist, limit, page, false, callback);
    }

    public void getArtistTopTracksSync(Artist artist, int limit, int page,
                                       boolean append, final GetResponseCallback callback) {
        final Params params = new Params(ARTIST_GET_TOP_TRACKS, ApiMethod.ARTIST_GET_TOP_TRACKS);
        params.putParameter("artist", Html.fromHtml(artist.getName())
                .toString());
        params.putParameter("api_key", apiKey);
        params.putParameter("limit", Integer.toString(limit));
        params.putParameter("page", Integer.toString(page));
        params.setApiSource(Params.ApiSource.LASTFM);
        if (append) {
            params.setAdditionalParameter("append");
        }
        doQuerySync(callback, params);
    }

    public void getAlbumInfo(Album album, final GetResponseCallback callback) {
        final Params params = new Params(ALBUM_GET_INFO, ApiMethod.ALBUM_GET_INFO);
        params.putParameter("artist", Html.fromHtml(album.getArtist())
                .toString());
        params.putParameter("album", Html.fromHtml(album.getTitle()).toString());
        params.putParameter("api_key", apiKey);
        params.setApiSource(Params.ApiSource.LASTFM);
        doQuery(callback, params);
    }

    public void getUserInfo(String name, final GetResponseCallback callback) {
        final Params params = new Params(USER_GET_INFO, ApiMethod.USER_GET_INFO);
        params.putParameter("user", Html.fromHtml(name).toString());
        params.putParameter("api_key", apiKey);
        params.setApiSource(Params.ApiSource.LASTFM);
        doQuery(callback, params);
    }

    public void getTrackInfo(Track track, final GetResponseCallback callback) {
        final Params params = new Params(TRACK_GET_INFO, ApiMethod.TRACK_GET_INFO);
        params.putParameter("track", Html.fromHtml(track.getTitle()).toString());
        params.putParameter("artist", Html.fromHtml(track.getArtist())
                .toString());
        params.putParameter("username",
                AuthorizationInfoManager.getLastfmName());
        params.putParameter("api_key", apiKey);
        params.setApiSource(Params.ApiSource.LASTFM);
        doQuery(callback, params);
    }

    public void getUserTopTracks(String name, String period, int limit,
                                 int page, boolean append, final GetResponseCallback callback) {
        final Params params = new Params(USER_GET_TOP_TRACKS, ApiMethod.USER_GET_TOP_TRACKS);
        params.putParameter("api_key", apiKey);
        params.putParameter("user", name);
        params.putParameter("period", period);
        params.putParameter("limit", Integer.toString(limit));
        params.putParameter("page", String.valueOf(page));
        params.setApiSource(Params.ApiSource.LASTFM);
        if (append) {
            params.setAdditionalParameter("append");
        }
        doQuery(callback, params);
    }

    public void getArtistImages(String artist, int page, final GetResponseCallback callback) {
        final Params params = new Params(null, ApiMethod.ARTIST_IMAGES);
        params.setApiSource(Params.ApiSource.STRAIGHT);
        params.setUrl(String.format("http://www.lastfm.ru/music/%s/+images?page=%d",
                StringUtils.encode(artist), page));
        doQuery(callback, params);
    }

    public void getRecommendedTracks(final List<Artist> artists,
                                     final GetResponseCallback callback) {
        final Set<Track> trackSet = new HashSet<>();
        final int[] completed = {0};
        for (int i = 0; i < artists.size(); i++) {
            Artist artist = artists.get(i);
            final int finalI = i;
            getArtistTopTracksSync(artist, 5, 0, new GetResponseCallback() {
                @Override
                public void onDataReceived(ReadyResult result) {
                    if (result.isOk()) {
                        List<Track> artistTopTracks = (List<Track>) result.getObject();
                        trackSet.addAll(artistTopTracks);
                    }
                    if (++completed[0] == artists.size()) {
                        List<Track> trackList = new ArrayList<>(trackSet);
                        Collections.shuffle(trackList);
                        callback.onDataReceived(new ReadyResult(RECOMMENDATIONS, trackList));
                    }
                }
            });
        }
    }

    public void getAlbumsInfo(final List<Album> albums, final GetResponseCallback callback) {
        final Set<Track> trackSet = new LinkedHashSet<>();
        final int[] completed = {0};
        for (int i = 0; i < albums.size(); i++) {
            Album album = albums.get(i);
            QueryManager.getInstance().getAlbumInfo(album, new GetResponseCallback() {
                @Override
                public void onDataReceived(ReadyResult result) {
                    if (result.isOk()) {
                        List<Object> list = (List<Object>) result.getObject();
                        List<Track> albumTracks = (List<Track>) list.get(1);
                        trackSet.addAll(albumTracks);
                    }
                    if (++completed[0] == albums.size()) {
                        List<Track> trackList = new ArrayList<>(trackSet);
                        callback.onDataReceived(new ReadyResult(RECOMMENDATIONS, trackList));
                    }
                }
            });
        }
    }

    public void getLovedTracks(String name, int limit, int page,
                               final GetResponseCallback callback) {
        final Params params = new Params(USER_GET_LOVED_TRACKS, ApiMethod.USER_GET_LOVED_TRACKS);
        params.putParameter("user", name);
        params.putParameter("api_key", apiKey);
        params.putParameter("limit", Integer.toString(limit));
        params.putParameter("page", Integer.toString(page));
        params.setApiSource(Params.ApiSource.LASTFM);
        doQuery(callback, params);
    }

    /**
     * @param callback returns ReadyResult object as List<Track>
     */
    public void getLibraryTracks(final String user, final GetResponseCallback callback) {
        final Set<Track> libraryTracks = new HashSet<>();
        QueryManager.getInstance().getLovedTracks(user, PreferencesManager.getModePreferences()
                .getInt(Constants.LOVED_IN_LIBRARY, 200), 0, new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (result.isOk()) {
                    final ArrayList<Track> lovedTracks = (ArrayList<Track>) result.getObject();
                    libraryTracks.addAll(lovedTracks);
                }
                QueryManager.getInstance().getUserTopTracks(user, Constants.PERIODS_ARRAY[0],
                        PreferencesManager.getModePreferences()
                                .getInt(Constants.TOP_TRACKS_IN_LIBRARY, 200), 0, true,
                        new GetResponseCallback() {
                            @Override
                            public void onDataReceived(ReadyResult result) {
                                if (result.isOk()) {
                                    final ArrayList<Track> topTracks = (ArrayList<Track>) result.getObject();
                                    libraryTracks.addAll(topTracks);
                                }
                                List<Track> trackList = new ArrayList<>(libraryTracks);
                                Collections.shuffle(trackList);
                                callback.onDataReceived(new ReadyResult(LIBRARY, trackList));
                            }
                        });
            }
        });
    }

    /**
     * @param callback returns ReadyResult object as List<Track>
     */
    public void getRadiomix(final String user, final GetResponseCallback callback) {
        final Set<Track> libraryTracks = new HashSet<>();
        QueryManager.getInstance().getLovedTracks(user,
                PreferencesManager.getModePreferences()
                        .getInt(Constants.LOVED_IN_RADIOMIX, 100), 0, new GetResponseCallback() {
                    @Override
                    public void onDataReceived(ReadyResult result) {
                        if (result.isOk()) {
                            final ArrayList<Track> lovedTracks = (ArrayList<Track>) result.getObject();
                            libraryTracks.addAll(lovedTracks);
                        }
                        QueryManager.getInstance().getUserTopTracks(user, Constants.PERIODS_ARRAY[0],
                                PreferencesManager.getModePreferences()
                                        .getInt(Constants.TOP_IN_RADIOMIX, 100), 0, true, new GetResponseCallback() {
                                    @Override
                                    public void onDataReceived(ReadyResult result) {
                                        if (result.isOk()) {
                                            final ArrayList<Track> topTracks = (ArrayList<Track>) result.getObject();
                                            libraryTracks.addAll(topTracks);
                                        }
                                        QueryManager.getInstance().getWeeklyTrackChart(user,
                                                PreferencesManager.getModePreferences()
                                                        .getInt(Constants.WEEKLY_IN_RADIOMIX, 200), true,
                                                new GetResponseCallback() {
                                                    @Override
                                                    public void onDataReceived(ReadyResult result) {
                                                        if (result.isOk()) {
                                                            final ArrayList<Track> weeklyTracks =
                                                                    (ArrayList<Track>) result.getObject();
                                                            libraryTracks.addAll(weeklyTracks);
                                                        }
                                                        List<Track> trackList = new ArrayList<>(libraryTracks);
                                                        Collections.shuffle(trackList);
                                                        callback.onDataReceived(new ReadyResult(LIBRARY, trackList));
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    public void getWeeklyTrackChart(String user, int count, boolean append,
                                    final GetResponseCallback callback) {
        final Params params = new Params(USER_GET_WEEKLY_TRACK_CHART,
                ApiMethod.USER_GET_WEEKLY_TRACK_CHART);
        params.putParameter("user", user);
        params.putParameter("count", Integer.toString(count));
        params.putParameter("api_key", apiKey);
        params.setApiSource(Params.ApiSource.LASTFM);
        if (append) {
            params.setAdditionalParameter("append");
        }
        doQuery(callback, params);
    }

    public void scrobbleOffline() {
        if (scrobbleThread != null) {
            scrobbleThread.setCancelled(true);
        }
        scrobbleThread = new CancellableThread(new Runnable() {
            @Override
            public void run() {
                final PlaylistManager playlistManager = PlaylistManager.getInstance();
                while (true) {
                    Track track = playlistManager.loadHeadTrackToScrobble();
                    if (track == null) break;
                    Map<String, String> params = StringUtils.map("api_key", apiKey,
                            "artist", Html.fromHtml(track.getArtist()).toString(), "track", Html
                                    .fromHtml(track.getTitle()).toString(), "timestamp",
                            String.valueOf(track.getScrobbleTime())
                    );
                    params.put("sk", sk);
                    String apiSig = createSignature("track.scrobble", params, secret);
                    params.put("api_sig", apiSig);
                    Params methodParams = new Params("track.scrobble",
                            ApiMethod.TRACK_SCROBBLE, params);
                    methodParams.setApiSource(Params.ApiSource.LASTFM);
                    if (scrobbleThread.isCancelled()) return;
                    new PostTask(new RestTaskCallback() {
                        @Override
                        public void onTaskComplete(ReadyResult result) {
                        }
                    }).doHttpQuery(methodParams);
                    playlistManager.removeHeadTrackToScrobble();
                }
            }
        });
        scrobbleThread.start();
    }

    public void getArtistInfo(String artist, String username, final GetResponseCallback callback) {
        final Params params = new Params(ARTIST_GET_INFO, ApiMethod.ARTIST_GET_INFO);
        params.putParameter("artist", Html.fromHtml(artist).toString());
        if (Locale.getDefault().getLanguage().equals("ru")) {
            params.putParameter("lang", "ru");
        } else if (Locale.getDefault().getLanguage().equals("es")) {
            params.putParameter("lang", "es");
        }
        if (username != null) {
            params.putParameter("username", username);
        }
        params.putParameter("api_key", apiKey);
        params.setApiSource(Params.ApiSource.LASTFM);
        doQuery(callback, params);
    }

    public void getSimilarArtists(String artist, final GetResponseCallback callback) {
        final Params params = new Params(ARTIST_GET_SIMILAR, ApiMethod.ARTIST_GET_SIMILAR);
        params.putParameter("artist", Html.fromHtml(artist).toString());
        params.putParameter("api_key", apiKey);
        params.setApiSource(Params.ApiSource.LASTFM);
        doQuery(callback, params);
    }

    public void searchArtist(String artist, final GetResponseCallback callback) {
        final Params params = new Params(ARTIST_SEARCH, ApiMethod.ARTIST_SEARCH);
        params.putParameter("artist", Html.fromHtml(artist).toString());
        params.putParameter("api_key", apiKey);
        params.setApiSource(Params.ApiSource.LASTFM);
        doQuery(callback, params);
    }

    public void searchTag(String tag, final GetResponseCallback callback) {
        final Params params = new Params(TAG_SEARCH, ApiMethod.TAG_SEARCH);
        params.putParameter("tag", Html.fromHtml(tag).toString());
        params.putParameter("api_key", apiKey);
        params.setApiSource(Params.ApiSource.LASTFM);
        doQuery(callback, params);
    }

    public void searchAlbum(String album, final GetResponseCallback callback) {
        final Params params = new Params(ALBUM_SEARCH, ApiMethod.ALBUM_SEARCH);
        params.putParameter("album", Html.fromHtml(album).toString());
        params.putParameter("api_key", apiKey);
        params.setApiSource(Params.ApiSource.LASTFM);
        doQuery(callback, params);
    }

    public void getVkUserAudio(long id, int count, int offset, final GetResponseCallback callback) {
        final Params params = new Params(AUDIO_GET, ApiMethod.AUDIO_GET);
        params.putParameter("oid", Long.toString(id));
        params.putParameter("offset", Integer.toString(offset));
        params.putParameter("count", Integer.toString(count));
        params.setApiSource(Params.ApiSource.VK);
        doQuery(callback, params);
    }

    public void getVkUserWallAudio(long id, int count, int offset, long gid,
                                   final GetResponseCallback callback) {
        final Params params = new Params(WALL_GET, ApiMethod.WALL_GET);
        String ownerId = id == -1 ? "-" + gid : String.valueOf(id);
        params.putParameter("owner_id", ownerId);
        params.putParameter("offset", Integer.toString(offset));
        params.putParameter("count", Integer.toString(count));
        params.setApiSource(Params.ApiSource.VK);
        doQuery(callback, params);
    }

    public void postVkUserWall(String message, String bitmap, final Track track,
                               final GetResponseCallback callback) {
        final Params params = new Params(WALL_POST, ApiMethod.WALL_POST);
        params.putParameter("message", message);
        final StringBuilder attachments = new StringBuilder();
        if (bitmap != null) {
            uploadBitmap(bitmap, new GetResponseCallback() {
                @Override
                public void onDataReceived(ReadyResult result) {
                    String photoId = (String) result.getObject();
                    attachments.append(photoId);
                    if (track.getOwnerId() != 0) {
                        if (photoId != null) {
                            attachments.append(",");
                        }
                        attachments.append("audio")
                                .append(track.getOwnerId()).append("_").append(track.getAid());
                    }
                    params.putParameter("attachments", attachments.toString());
                    params.setApiSource(Params.ApiSource.VK);
                    doQuery(callback, params);
                }
            });
        } else {
            if (track.getOwnerId() != 0) {
                attachments.append("audio").append(track.getOwnerId())
                        .append("_").append(track.getAid());
            }
            params.putParameter("attachments", attachments.toString());
            params.setApiSource(Params.ApiSource.VK);
            doQuery(callback, params);
        }
    }

    public void getTrackUrl(Track track, boolean current, int aimUrl,
                            final GetResponseCallback callback) {
        boolean live = track.isLive();
        final Params params = new Params(live ? EXECUTE_LIVE_URL :
                EXECUTE_URL, ApiMethod.EXECUTE_URL);
        String request = escapeString(track.getNotation());
        params.putParameter("q", request);
        params.putParameter("n", Integer.toString(aimUrl + 1));
        params.setApiSource(Params.ApiSource.VK);
        if (current) {
            params.setAdditionalParameter("current");
        } else {
            params.setAdditionalParameter("not_current");
        }
        doQuery(callback, params);
    }

    public void getTrackLyrics(Track track, int lyricsNumber, final GetResponseCallback callback) {
        getTrackLyrics(track.getNotation(), lyricsNumber, callback);
    }

    public String escapeString(String s) {
        return Html.fromHtml(s).toString().replaceAll("[\\?&!@#$%^*()_+{}]", "");
    }

    public void getTrackLyrics(String q, int count, final GetResponseCallback callback) {
        final Params params = new Params(EXECUTE_LYRICS, ApiMethod.EXECUTE_LYRICS);
        String request = escapeString(q);
        params.putParameter("q", request);
        params.putParameter("aim", Integer.toString(count + 1));
        params.setApiSource(Params.ApiSource.VK);
        doQuery(callback, params);
    }

    public void getUserTopArtists(String name, String period, int limit, int page,
                                  GetResponseCallback callback) {
        final Params params = new Params(USER_GET_TOP_ARTISTS, ApiMethod.USER_GET_TOP_ARTISTS);
        params.putParameter("api_key", apiKey);
        params.putParameter("user", name);
        params.putParameter("period", period);
        params.putParameter("limit", Integer.toString(limit));
        params.putParameter("page", String.valueOf(page));
        params.setApiSource(Params.ApiSource.LASTFM);
        doQuery(callback, params);
    }

    public void getPersonalArtistTop(String artist, String username, int limit, int page,
                                     GetResponseCallback callback) {
        final Params params = new Params(LIBRARY_GET_TRACKS, ApiMethod.LIBRARY_GET_TRACKS);
        params.putParameter("artist", Html.fromHtml(artist).toString());
        params.putParameter("api_key", apiKey);
        params.putParameter("user", username);
        params.putParameter("limit", Integer.toString(limit));
        params.putParameter("page", Integer.toString(page));
        params.setApiSource(Params.ApiSource.LASTFM);
        doQuery(callback, params);
    }

    public void uploadBitmap(final String imageUrl, final GetResponseCallback callback) {
        final File cachedImage = ImageLoader.getInstance().getDiscCache().get(imageUrl);
        getPhotosWallUploadServer(new GetResponseCallback() {
            @Override
            public void onDataReceived(final ReadyResult result) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadUserPhoto(cachedImage, (String) result.getObject(), callback);
                    }
                }).start();
            }
        });
    }

    private void saveWallPhoto(String server, String photo, String hash,
                               GetResponseCallback callback) {
        final Params params = new Params(PHOTOS_SAVE_WALL_PHOTO,
                ApiMethod.PHOTOS_SAVE_WALL_PHOTO);
        params.putParameter("server", server);
        params.putParameter("photo", photo);
        params.putParameter("hash", hash);
        params.setApiSource(Params.ApiSource.VK);
        doQuery(callback, params);
    }

    public void getPhotosWallUploadServer(GetResponseCallback callback) {
        final Params params = new Params(PHOTOS_GET_WALL_UPLOAD_SERVER,
                ApiMethod.PHOTOS_GET_WALL_UPLOAD_SERVER);
        params.setApiSource(Params.ApiSource.VK);
        doQuery(callback, params);
    }

    public void uploadUserPhoto(File image, String server, GetResponseCallback callback) {
        try {
            HttpPost httppost = new HttpPost(server);
            MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
            multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            ContentBody cbFile = new FileBody(image,
                    ContentType.create("image/jpeg"), "photo.jpg");
            multipartEntity.addPart("photo", cbFile);
            httppost.setEntity(multipartEntity.build());
            mHttpClient.execute(httppost, new PhotoUploadResponseHandler(callback));

        } catch (Exception ignored) {
        }
    }

    public void getAlbumImage(final Album album, final CompletionListener listener) {
        if (album == null) return;
        imageLoader.loadImage(LiqearApplication.getAppContext(),
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
                        listener.onCompleted();
                    }

                    @Override
                    public void onLoadingCancelled() {

                    }
                });
    }

    private class PhotoUploadResponseHandler implements ResponseHandler<Object> {

        private final GetResponseCallback callback;

        public PhotoUploadResponseHandler(GetResponseCallback callback) {
            this.callback = callback;
        }

        @Override
        public Object handleResponse(HttpResponse response)
                throws IOException {

            HttpEntity r_entity = response.getEntity();
            String responseString = EntityUtils.toString(r_entity);
            Result uploadResult = new Result(responseString, null, null, null);
            ReadyResult readyResult = Parser.getInstance(uploadResult,
                    ApiMethod.UPLOAD_PHOTO).parse();
            List<String> list = (List<String>) readyResult.getObject();
            saveWallPhoto(list.get(0), list.get(1), list.get(2), new GetResponseCallback() {
                @Override
                public void onDataReceived(ReadyResult result) {
                    callback.onDataReceived(result);
                }
            });

            return null;
        }

    }

}



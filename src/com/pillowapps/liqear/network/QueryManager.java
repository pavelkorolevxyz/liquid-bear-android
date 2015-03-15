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
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.PlaylistManager;
import com.pillowapps.liqear.helpers.StringUtils;

import org.apache.http.HttpVersion;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import java.util.Map;
import java.util.TreeMap;

/**
 * Entry point into the API.
 */
@SuppressWarnings({"unchecked"})
public class QueryManager {
    public static final String SETLISTS = "setlists";
    private final String secret;
    private final String apiKey;
    private final String sk;
    private GetTask task;
    private CancellableThread scrobbleThread;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public QueryManager() {
        this.apiKey = AuthorizationInfoManager.getLastfmApiKey();
        this.sk = AuthorizationInfoManager.getLastfmKey();
        this.secret = AuthorizationInfoManager.getLastfmSecret();
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
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

    public void getArtistImages(String artist, int page, final GetResponseCallback callback) {
        final Params params = new Params(null, ApiMethod.ARTIST_IMAGES);
        params.setApiSource(Params.ApiSource.STRAIGHT);
        params.setUrl(String.format("http://www.lastfm.ru/music/%s/+images?page=%d",
                StringUtils.encode(artist), page));
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

    public void getAlbumImage(final Album album, final CompletionListener listener) {
        if (album == null) {
            AudioTimeline.setCurrentAlbumBitmap(null);
            listener.onCompleted();
            return;
        }
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
}



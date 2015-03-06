package com.pillowapps.liqear.network;

import android.content.Intent;
import android.os.AsyncTask;

import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activity.AuthActivity;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.models.ErrorResponseLastfm;
import com.pillowapps.liqear.models.ErrorResponseVk;
import com.pillowapps.liqear.models.Setlist;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

/**
 * An AsyncTask implementation for performing GETs on the Hypothetical REST APIs.
 */
public class GetTask extends AsyncTask<Params, Void, ReadyResult> {

    private RestTaskCallback mCallback;

    public GetTask(RestTaskCallback callback) {
        this.mCallback = callback;
    }

    @Override
    protected ReadyResult doInBackground(Params... params) {
        return doHttpQuery(params[0]);
    }

    public ReadyResult doHttpQuery(Params params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        InputStream stream = null;
        InputStreamReader inputStreamReader = null;
        final String urlParameters = params.buildParameterQueue();
        try {
            if (isCancelled()) return null;
            String separator = params.getApiSource() != Params.ApiSource.SETLISTFM ? "?" : "";
            StringBuilder s = new StringBuilder();
            String inputLine;
            System.setProperty("http.keepAlive", "false");
            if (isCancelled()) return null;
            urlConnection = openConnection(params.getUrl() + separator + urlParameters);
            if (isCancelled()) return null;
            urlConnection.setRequestProperty("Accept-Encoding", "gzip");
            stream = urlConnection.getInputStream();
            if ("gzip".equals(urlConnection.getContentEncoding())) {
                stream = new GZIPInputStream(stream);
            }
            if (isCancelled()) return null;
            inputStreamReader = new InputStreamReader(stream);
            reader = new BufferedReader(inputStreamReader);
            while ((inputLine = reader.readLine()) != null) {
                s.append(inputLine);
            }
            String result = s.toString();

            if (isCancelled()) return null;
            ReadyResult readyResult = Parser.getInstance(new Result(
                    result,
                    params.getApiSource(),
                    params.getMethodString(),
                    null
            ), params.getMethodEnum()).parse();
            if (!readyResult.isOk()) {
                Object resultObject = readyResult.getObject();
                if (resultObject == null) return null;
                if (params.getApiSource() == Params.ApiSource.LASTFM) {
                    if (resultObject instanceof ErrorResponseLastfm) {
                        if (((ErrorResponseLastfm) resultObject).getError() == 4/*Auth problem Last.fm*/) {
                            AuthorizationInfoManager.signOutLastfm();
                            Intent intent = new Intent(LiqearApplication.getAppContext(),
                                    AuthActivity.class);
                            intent.putExtra(Constants.AUTH_PROBLEMS, 1/*Lastfm*/);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            LiqearApplication.getAppContext().startActivity(intent);
                            return null;
                        }
                    }
                } else if (params.getApiSource() == Params.ApiSource.VK) {
                    if (resultObject instanceof ErrorResponseVk) {
                        if (((ErrorResponseVk) resultObject).getErrorCode() == 5/*Auth problem VK*/) {
                            AuthorizationInfoManager.signOutVk();
                            Intent intent = new Intent(LiqearApplication.getAppContext(),
                                    AuthActivity.class);
                            intent.putExtra(Constants.AUTH_PROBLEMS, 2/*VK*/);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            LiqearApplication.getAppContext().startActivity(intent);
                            return null;
                        }
                    }
                    return null;
                }
            }
            return readyResult;
        } catch (FileNotFoundException e1) {
            return getErrorResult(params);
        } catch (IOException e) {
            return getErrorResult(params);
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException ignored) {
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private ReadyResult getErrorResult(Params params) {
        if (params.getApiSource() == Params.ApiSource.LASTFM) {
            return new ReadyResult(params.getMethodString(), (long) 0, ApiMethod.ERROR,
                    new ErrorResponseLastfm(0, LiqearApplication.getAppContext().getString(R.string.unexpected_error)));
        } else if (params.getApiSource() == Params.ApiSource.VK) {
            return new ReadyResult(params.getMethodString(), (long) 0, ApiMethod.ERROR,
                    new ErrorResponseVk(LiqearApplication.getAppContext().getString(R.string.unexpected_error)));
        } else if (params.getApiSource() == Params.ApiSource.SETLISTFM) {
            return new ReadyResult(params.getMethodString(), new ArrayList<Setlist>(1),
                    params.getMethodEnum());
        }
        return new ReadyResult(params.getMethodString(), null, ApiMethod.ERROR);
    }

    public HttpURLConnection openConnection(String url) throws IOException {
        URL u = new URL(url);
        return (HttpURLConnection) u.openConnection();
    }

    @Override
    protected void onPostExecute(ReadyResult result) {
        if (result == null) return;
        mCallback.onTaskComplete(result);
        super.onPostExecute(result);
    }
}


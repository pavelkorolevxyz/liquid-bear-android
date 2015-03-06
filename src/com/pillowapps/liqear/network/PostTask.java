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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * An AsyncTask implementation for performing POSTs on the Hypothetical REST APIs.
 */
public class PostTask extends AsyncTask<Params, String, ReadyResult> {
    private RestTaskCallback mCallback;

    public PostTask(RestTaskCallback callback) {
        this.mCallback = callback;
    }

    @Override
    protected ReadyResult doInBackground(Params... params) {
        return doHttpQuery(params[0]);
    }

    public ReadyResult doHttpQuery(Params params) {
        HttpURLConnection urlConnection = null;
        OutputStream outputStream = null;
        BufferedWriter writer = null;
        BufferedReader reader = null;
        InputStream stream = null;
        InputStreamReader inputStreamReader = null;
        final String urlParameters = params.buildParameterQueue();
        try {
            String separator = params.getApiSource() != Params.ApiSource.SETLISTFM ? "?" : "";
            StringBuilder s = new StringBuilder();
            String inputLine;
            System.setProperty("http.keepAlive", "false");
            urlConnection = openConnection(params.getUrl() + separator + urlParameters);

            urlConnection.setRequestProperty("Accept-Encoding", "gzip");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(60 * 1000);
            outputStream = urlConnection.getOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(urlParameters);
            stream = urlConnection.getInputStream();

            if ("gzip".equals(urlConnection.getContentEncoding())) {
                stream = new GZIPInputStream(stream);
            }
            inputStreamReader = new InputStreamReader(stream);
            reader = new BufferedReader(inputStreamReader);
            while ((inputLine = reader.readLine()) != null) {
                s.append(inputLine);
            }
            ReadyResult readyResult = Parser.getInstance(new Result(
                    s.toString(),
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
        } catch (FileNotFoundException e) {
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
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
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
            return new ReadyResult(params.getMethodString(), (long) 0, ApiMethod.ERROR, new ErrorResponseLastfm(0, LiqearApplication.getAppContext().getString(R.string.unexpected_error)));
        } else if (params.getApiSource() == Params.ApiSource.VK) {
            return new ReadyResult(params.getMethodString(), (long) 0, ApiMethod.ERROR, new ErrorResponseVk(LiqearApplication.getAppContext().getString(R.string.unexpected_error)));
        }
        return new ReadyResult(params.getMethodString(), null, ApiMethod.ERROR);
    }

    public HttpURLConnection openConnection(String url) throws IOException {
        URL u = new URL(url);
        return (HttpURLConnection) u.openConnection();
    }

    @Override
    protected void onPostExecute(ReadyResult result) {
        mCallback.onTaskComplete(result);
        super.onPostExecute(result);
    }
}

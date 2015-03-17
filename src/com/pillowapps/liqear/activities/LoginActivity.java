package com.pillowapps.liqear.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.network.ServiceHelper;

public class LoginActivity extends TrackedActivity {
    public static final String OAUTH_REQUEST_FORMAT = "http://oauth.vk.com/authorize?"
            + "client_id=%s&"
            + "scope=%s&"
            + "redirect_uri=%s&"
            + "display=%s&" + "response_type=%s";
    public static final String allPermission = "friends,photos,audio,status,wall,groups,offline";
    public static final String OAUT_BLANK_URL = "http://api.vkontakte.ru/blank.html";
    public static final String DISPLAY = "touch";
    public static final String RESPONSE = "token";
    private ProgressBar progressBar;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vk_login);
        WebView webview = (WebView) findViewById(R.id.login_webview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webview.clearCache(true);

        webview.setWebViewClient(new LoginWebViewClient());

        CookieSyncManager.createInstance(this);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        String url = String.format(OAUTH_REQUEST_FORMAT, ServiceHelper.VK_APP_ID,
                allPermission, OAUT_BLANK_URL, DISPLAY, RESPONSE);
        webview.loadUrl(url);
    }

    private void parseUrl(String url) {
        try {
            if (url.startsWith(OAUT_BLANK_URL)) {
                setUserConfiguration(url.split("[#&]"));
                Intent data = new Intent();
                setResult(RESULT_OK, data);
                finish();
            }
        } catch (Exception ignored) {
        }
    }

    private void setUserConfiguration(String[] strings) throws Exception {
        if (strings.length < 4) {
            return;
        }
        boolean hasAccessToken = false;
        boolean hasUserId = false;
        String accessToken = null;
        Long userId = -1l;
        String secret = null;
        for (String param : strings) {
            if (param.startsWith("access_token")) {
                accessToken = param.substring(param.indexOf("=") + 1);
                hasAccessToken = true;
            }
            if (param.startsWith("user_id")) {
                userId = Long.valueOf(param.substring(param.indexOf("=") + 1));
                hasUserId = true;
            }
            if (param.startsWith("secret")) {
                secret = param.substring(param.indexOf("=") + 1);
            }
        }
        if (hasAccessToken && hasUserId) {
            SharedPreferences preferences = LBApplication.getAppContext()
                    .getSharedPreferences(AuthorizationInfoManager.VK_PREFERENCES,
                            Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("access_token", accessToken);
            editor.putLong("uid", userId);
            editor.putString("secret", secret);
            editor.apply();
        } else {
            throw new Exception("Invalid authorization request");
        }

    }

    private class LoginWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.GONE);
            parseUrl(url);
        }
    }
}

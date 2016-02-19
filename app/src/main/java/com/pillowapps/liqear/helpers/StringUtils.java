package com.pillowapps.liqear.helpers;

import android.text.Html;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

public class StringUtils {

    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private StringUtils() {
        // no-op
    }

    public static String md5(String s) {
        byte[] bytes;
        try {
            bytes = digest.digest(s.getBytes("UTF-8"));

            StringBuilder b = new StringBuilder(32);
            for (byte aByte : bytes) {
                String hex = Integer.toHexString((int) aByte & 0xFF);
                if (hex.length() == 1)
                    b.append('0');
                b.append(hex);
            }
            return b.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    public static String escapeString(String s) {
        return Html.fromHtml(s).toString().replaceAll("[\\?&!@#$%^*()_+{}]", "");
    }

    public static Map<String, String> parseUrlParams(URL url) {
        Map<String, String> params = new LinkedHashMap<>();
        try {
            String query = url.getQuery();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                params.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                        URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
        } catch (UnsupportedEncodingException ignored) {

        }
        return params;
    }

    public static String trim(String s) {
        if (s == null) {
            return null;
        }
        return s.trim();
    }
}

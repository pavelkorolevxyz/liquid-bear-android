package com.pillowapps.liqear.helpers;

import android.text.Html;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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

    public static Map<String, String> map(String... strings) {
        if (strings.length % 2 != 0)
            throw new IllegalArgumentException("strings.length % 2 != 0");
        Map<String, String> mp = new HashMap<String, String>();
        for (int i = 0; i < strings.length; i += 2) {
            mp.put(strings[i], strings[i + 1]);
        }
        return mp;
    }

    public static String encode(String s) {

        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String escapeString(String s) {
        return Html.fromHtml(s).toString().replaceAll("[\\?&!@#$%^*()_+{}]", "");
    }
}

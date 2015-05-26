package com.pillowapps.liqear.network;

import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public Parser() {
    }

    public List<String> parseGetArtistImages(Response response) {
        List<String> imagesUrls = new ArrayList<>(36);
        String text;
        try {
            text = response.body().string();
        } catch (IOException e) {
            return imagesUrls;
        }
        String patternString = "http://userserve-ak.last.fm/serve/126s/";
        String patternNeed = "http://userserve-ak.last.fm/serve/500/";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int end = matcher.end();
            String imageId = text.substring(end, text.indexOf("\"", end));
            imagesUrls.add(patternNeed.concat(imageId));
        }
        return imagesUrls;
    }
}
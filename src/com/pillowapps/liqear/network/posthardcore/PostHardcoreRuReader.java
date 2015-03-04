package com.pillowapps.liqear.network.posthardcore;

import com.pillowapps.liqear.models.Album;
import com.pillowapps.liqear.network.funkysouls.NewcomersReaderException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostHardcoreRuReader {
    private static final String HOST = "http://post-hardcore.ru/music/page/";
    private static final String HOST_MAIN = "http://post-hardcore.ru/";
    private static final String TAG = "PostHardcoreReader";
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
    private Map<String, String> cookies;


    private ArrayList<Album> selectAlbums(String url) {
        try {
            if (cookies == null) {
                String firstUrl = HOST_MAIN;
                cookies = Jsoup.connect(firstUrl).execute().cookies();
            }
            Document albumDoc = Jsoup.parse(new String(
                    Jsoup.connect(url).cookies(cookies)
                            .userAgent(USER_AGENT).execute().bodyAsBytes(), "CP1251"));
            ArrayList<Album> albums = new ArrayList<Album>();
            Elements albumsTags = albumDoc.select("div#dle-content").select("div[id^=news]");
            for (Element albumTag : albumsTags) {
                try {
                    Album album = selectAlbumInfo(albumTag);
                    if (album == null) continue;
                    String html = albumTag.html();
                    Pattern pattern = Pattern.compile("\\d{1,2}\\. [\\w\\p{Punct}\\s]+");
                    Matcher matcher = pattern.matcher(html);
                    while (matcher.find()) {
                        matcher.start();
                        String track = matcher.group().replaceFirst("\\d{1,2}\\. ", "")
                                .replaceAll("\\(?\\d{1,2}:\\d{1,2}\\)?", "");
                        album.add(track);
                    }
                    if (album.getTracks().size() != 0 && !albums.contains(album)) {
                        albums.add(album);
                    }
                } catch (NewcomersReaderException ignored) {
                }
            }
            return albums;
        } catch (IOException e) {
            return new ArrayList<Album>();
        }
    }

    private Album selectAlbumInfo(Element albumTag) throws NewcomersReaderException {
        Album album = new Album();
        Element cover = albumTag.select("img").first();
        String coverSrc = HOST_MAIN + cover.attr("src");
        album.setImageUrl(coverSrc);
        String titleLine = cover.attr("title");
        if (titleLine.startsWith("VA")) return null;
        if (!titleLine.contains(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))
                && !titleLine.contains(String.valueOf(
                Calendar.getInstance().get(Calendar.YEAR) - 1)))
            return null;
        int artistNameLength = titleLine.indexOf("-");

        if (artistNameLength == -1) {
            artistNameLength = titleLine.indexOf("–");
            if (artistNameLength == -1) {
                return null;
            }
        }
        album.setArtist(titleLine.substring(0, artistNameLength).trim());
        if (artistNameLength != titleLine.length()) {
            album.setTitle(titleLine.substring(artistNameLength + 1)
                    .replaceAll("\\(\\d{4}\\)", ""));
        }
        album.setGenre("post hardcore");
        return album;

    }

    public List<Album> selectAlbumsFromPages(List<Integer> pages) {
        ArrayList<Album> albums = new ArrayList<Album>();
        for (int i : pages) {
            albums.addAll(selectAlbums(HOST + i + "/?action=mobile"));
        }
        return albums;
    }
}

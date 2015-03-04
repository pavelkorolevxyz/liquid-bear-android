package com.pillowapps.liqear.network.funkysouls;

import com.pillowapps.liqear.models.Album;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunkySoulsReader {
    private static final String FUNKY_TAG = "http://funkysouls.com/tag/";
    private static final String HOST = "http://funkysouls.com/music/";
    private static final String TAG = "FunkySoulsReader";


    private ArrayList<Album> selectAlbums(String url) {
        try {
            Document albumDoc = Jsoup.parse(new String(
                    Jsoup.connect(url).execute().bodyAsBytes(), "UTF-8"));
            ArrayList<Album> albums = new ArrayList<Album>();
            Elements albumsTags = albumDoc.select("article");
            for (Element albumsTag : albumsTags) {
                try {
                    Album album = selectAlbumInfo(albumsTag);
                    if (album == null) continue;
                    String html = albumsTag.html();
                    Pattern pattern = Pattern.compile("[0123456789]{1,2}\\. [\\w\\p{Punct} ]+");
                    Matcher matcher = pattern.matcher(html);
                    while (matcher.find()) {
                        matcher.start();
                        album.add(matcher.group().replaceFirst("[0123456789]{1,2}\\. ", ""));
                    }
                    albums.add(album);
                } catch (NewcomersReaderException ignored) {
                }
            }
            return albums;
        } catch (IOException e) {
            return new ArrayList<Album>();
        }
    }

    private Album selectAlbumInfo(Element albumsTag) throws NewcomersReaderException {
        Album album = new Album();
        Element coverTag = albumsTag.select("img").first();
        String coverSrc = coverTag.attr("src");
        String titleLine = coverTag.attr("alt");
        album.setImageUrl(coverSrc);
        Elements genreTag = albumsTag.select("a[href^=" + FUNKY_TAG + "]");
        for (Element element : genreTag) {
            if (album.getGenre().equals("")) {
                album.setGenre(element.text());
            } else {
                album.setGenre(album.getGenre() + " / " + element.text());
            }
        }

        if (titleLine.toLowerCase().contains("funkysouls") || titleLine.startsWith("VA"))
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
                    .replaceAll("\\[\\d{4}\\]", ""));
        }
        return album;

    }

    public List<Album> selectAlbumsFromPages(List<Integer> pages) {
        ArrayList<Album> albums = new ArrayList<Album>();
        String res;
        for (int i : pages) {
            res = i == 1 ? "index.html" : "page/" + i + ".html";
            albums.addAll(selectAlbums(HOST + res));
        }
        return albums;
    }
}

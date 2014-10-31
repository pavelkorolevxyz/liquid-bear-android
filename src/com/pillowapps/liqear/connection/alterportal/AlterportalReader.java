package com.pillowapps.liqear.connection.alterportal;

import com.pillowapps.liqear.connection.funkysouls.NewcomersReaderException;
import com.pillowapps.liqear.helpers.LLog;
import com.pillowapps.liqear.models.Album;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlterportalReader {
    private static final String HOST = "http://alterportal.ru/page/";


    private ArrayList<Album> selectAlbums(String url) {
        try {
            Document albumDoc = Jsoup.parse(new String(
                    Jsoup.connect(url).execute().bodyAsBytes(), "CP1251"));
            ArrayList<Album> albums = new ArrayList<Album>();
            Elements albumsTags = albumDoc.select("div#dle-content")
                    .select("table[width=530][border=0][cellspacing=0][cellpadding=0]");
            for (Element albumsTag : albumsTags) {
                try {
                    Album album = selectAlbumInfo(albumsTag.getElementsByTag("tr").get(3));
                    if (album == null) continue;
                    String html = albumsTag.html();
                    Pattern pattern = Pattern.compile("\\d{1,2}\\. [\\w\\p{Punct}\\s]+");
                    Matcher matcher = pattern.matcher(html);
                    while (matcher.find()) {
                        matcher.start();
                        String track = matcher.group().replaceFirst("\\d{1,2}\\. ", "")
                                .replaceAll("\\(?\\d{1,2}:\\d{1,2}\\)?", "");
                        album.add(track);
                    }
                    if (album.getTracks().size() != 0) {
                        albums.add(album);
                    }
                } catch (NewcomersReaderException e) {
                    LLog.log(e.getMessage());
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
        if (cover == null) {
            return null;
        }
        String coverSrc = cover.attr("src");
        album.setImageUrl(coverSrc);
        String titleLine = cover.attr("title");
        if (titleLine.toLowerCase().contains("alterportal")
                || titleLine.startsWith("VA")) return null;
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
        String regularExpression = "Стиль\\s?:(.*?)Треклист";
        Pattern genrePattern = Pattern.compile(regularExpression,
                Pattern.DOTALL & Pattern.CASE_INSENSITIVE);
        Matcher matcher = genrePattern.matcher(albumTag.text());
        while (matcher.find()) {
            String genreString = matcher.group(1);
            if (genreString.contains("Формат")) {
                genreString = genreString.substring(0, genreString.indexOf("Формат"));
            }
            if (genreString.contains("Качество")) {
                genreString = genreString.substring(0, genreString.indexOf("Качество"));
            }
            album.setGenre(genreString);
        }
        return album;

    }

    public List<Album> selectAlbumsFromPages(List<Integer> pages) {
        ArrayList<Album> albums = new ArrayList<Album>();
        for (int i : pages) {
            albums.addAll(selectAlbums(HOST + i + "/"));
        }
        return albums;
    }
}

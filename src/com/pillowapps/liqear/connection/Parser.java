package com.pillowapps.liqear.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.models.Album;
import com.pillowapps.liqear.models.Artist;
import com.pillowapps.liqear.models.ErrorResponseLastfm;
import com.pillowapps.liqear.models.ErrorResponseVk;
import com.pillowapps.liqear.models.Group;
import com.pillowapps.liqear.models.Session;
import com.pillowapps.liqear.models.Setlist;
import com.pillowapps.liqear.models.Tag;
import com.pillowapps.liqear.models.Track;
import com.pillowapps.liqear.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private String method;
    private ApiMethod methodEnum;
    private JSONObject json;
    private Gson gson;
    private String text;

    public Parser() {
        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
    }

    public static Parser getInstance(Result result, ApiMethod methodEnum) {
        Parser parser = new Parser();
        parser.setResult(result);
        parser.setMethodEnum(methodEnum);
        return parser;
    }

    public void setMethodEnum(ApiMethod methodEnum) {
        this.methodEnum = methodEnum;
    }

    public void setResult(Result result) {
        this.method = result.getMethod();
        if (result.getResultDocument() == null) {
            this.text = result.getResultDocumentString();
        } else {
            this.json = result.getResultDocument();
        }
    }

    private ReadyResult parseErrorLastfm() {
        try {
            return new ReadyResult(method, gson.fromJson(json.toString(),
                    ErrorResponseLastfm.class), ApiMethod.ERROR);
        } catch (Exception e) {
            return new ReadyResult(method, (long) 0, ApiMethod.ERROR,
                    new ErrorResponseLastfm(0, LiqearApplication.getAppContext().getString(R.string.unexpected_error)));
        }
    }

    private ReadyResult parseErrorVk() {
        try {
            ErrorResponseVk object = gson.fromJson(
                    new JSONObject(json.toString()).get("error").toString(), ErrorResponseVk.class);
            return new ReadyResult(method, object, ApiMethod.ERROR);
        } catch (Exception e) {
            return new ReadyResult(method, (long) 0, ApiMethod.ERROR,
                    new ErrorResponseVk(LiqearApplication.getAppContext().getString(R.string.unexpected_error)));
        }
    }

    public ReadyResult parseNeighbours() {
        try {
            List<User> neighbourList = new ArrayList<User>();
            JSONArray neighbours = json.getJSONObject("neighbours").getJSONArray("user");
            for (int i = 0; i < neighbours.length(); i++) {
                try {
                    JSONObject neighbour = neighbours.getJSONObject(i);
                    neighbourList.add(new User(neighbour.optString("name")));
                    neighbourList.get(i).setImageUrl(neighbour.getJSONArray("image")
                            .getJSONObject(3).optString("#text"));
                    neighbourList.get(i).setMatch(neighbour.getDouble("match"));
                } catch (JSONException ignored) {
                }
            }
            return new ReadyResult(method, neighbourList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseGetMobileSession() {
        try {
            Session result = gson.fromJson(json.getJSONObject("session").toString(), Session.class);
            return new ReadyResult(method, result);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseRecommendedArtists() {
        try {
            List<Artist> artistList = new ArrayList<Artist>();
            JSONArray artists = json
                    .getJSONObject("recommendations")
                    .getJSONArray("artist");
            for (int i = 0; i < artists.length(); i++) {
                try {
                    JSONObject artist = artists.getJSONObject(i);
                    artistList.add(new Artist(artist.optString("name")));
                    artistList.get(i).setPreviewUrl(
                            artist.getJSONArray("image").getJSONObject(3)
                                    .optString("#text")
                    );
                } catch (JSONException ignored) {
                }
            }

            return new ReadyResult(method, artistList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseFriendsLastfm() {
        try {
            List<User> friendList = new ArrayList<User>();
            JSONArray friends = json.getJSONObject("friends").getJSONArray("user");
            for (int i = 0; i < friends.length(); i++) {
                try {
                    JSONObject friend = friends.getJSONObject(i);
                    friendList.add(new User(friend.optString("name")));
                    friendList.get(i).setAge(
                            friend.optString("age").equals("") ? null : friend
                                    .optInt("age")
                    );
                    friendList.get(i).setCountry(friend.optString("country"));
                    friendList.get(i).setPlaycount(friend.optLong("playcount"));
                    friendList.get(i).setImageUrl(
                            friend.getJSONArray("image").getJSONObject(3)
                                    .optString("#text")
                    );
                } catch (JSONException ignored) {
                }
            }
            return new ReadyResult(method, friendList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseTagTopTracks() {
        try {
            Object object = json.getJSONObject("toptracks").get("track");
            List<Track> trackList = getTracksArray(object);
            return new ReadyResult(method, trackList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseArtistTopTracks() {
        try {
            Object object = json.getJSONObject("toptracks").get("track");
            List<Track> trackList = getTracksArray(object);
            return new ReadyResult(method, trackList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseAlbumGetInfo() {
        try {
            Album album = new Album();
            JSONObject albumJson = json.getJSONObject("album");
            album.setTitle(albumJson.getString("name"));
            album.setArtist(albumJson.getString("artist"));
            album.setImageUrl(setMaxImageUrl(albumJson.getJSONArray("image")));
            album.setPublishDate(albumJson.optString("releasedate"));
            List<Track> trackList = new ArrayList<Track>(1);
            try {
                Object object = albumJson.getJSONObject("tracks").get("track");
                trackList = getTracksArray(object);
            } catch (Exception ignored) {
            }
            return new ReadyResult(method, Arrays.asList(album, trackList));
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    private List<Track> getTracksArray(Object object) {
        List<Track> trackList = new ArrayList<Track>();
        if (object instanceof JSONArray) {
            JSONArray tracks = (JSONArray) object;
            for (int i = 0; i < tracks.length(); i++) {
                try {
                    JSONObject track = tracks.getJSONObject(i);
                    JSONObject artistJson = track.getJSONObject("artist");
                    String artist = artistJson.optString("name");
                    int playcount = track.optInt("playcount");
                    if (artist.length() == 0) artist = artistJson.optString("#text");
                    Track trackToAdd = new Track(artist, track.optString("name"));
                    trackToAdd.setUserPlayCount(playcount);
                    trackList.add(trackToAdd);
                } catch (JSONException ignored) {
                    ignored.printStackTrace();
                }
            }
        } else {
            try {
                JSONObject track = (JSONObject) object;
                JSONObject artistJson = track.getJSONObject("artist");
                String artist = artistJson.optString("name");
                if (artist.length() == 0) artist = artistJson.optString("#text");
                trackList.add(new Track(artist, track.optString("name")));
            } catch (JSONException ignored) {
                ignored.printStackTrace();
            }
        }
        return trackList;
    }

    public ReadyResult parseUserGetInfo() {
        try {
            JSONObject trackJson = json.getJSONObject("user");
            String url = setMaxImageUrl(trackJson.getJSONArray("image"));
            return new ReadyResult(method, url);
        } catch (JSONException e) {
            e.printStackTrace();
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseGetTrackInfo() {
        JSONObject trackJSON;
        final boolean isLoved;
        trackJSON = json.optJSONObject("track");
        if (trackJSON == null) {
            return parseErrorLastfm();
        }
        JSONObject albumJSON = trackJSON.optJSONObject("album");
        final Album album = new Album();
        if (albumJSON != null) {
            album.setArtist(albumJSON.optString("artist"));
            album.setTitle(albumJSON.optString("title"));
            JSONArray image = albumJSON.optJSONArray("image");
            album.setImageUrl(setMaxImageUrl(image));
        }
        isLoved = trackJSON.optInt("userloved") == 1;
        return new ReadyResult(method, Arrays.asList(album, isLoved));
    }

    private String setMaxImageUrl(JSONArray image) {
        for (int i = 4; i >= 0; i--) {
            JSONObject imageItem = image.optJSONObject(i);
            if (imageItem == null) continue;
            String imageUrl = imageItem.optString("#text");
            if (imageUrl != null) {
                return imageUrl;
            }
        }
        return null;
    }

    public ReadyResult parseGetUserLibrary() {
        Collection<Track> trackList = new TreeSet<Track>(new Comparator<Track>() {
            @Override
            public int compare(Track track, Track track2) {
                long userPlayCount = track.getUserPlayCount();
                long userPlayCount1 = track2.getUserPlayCount();
                if (userPlayCount > userPlayCount1) {
                    return -1;
                } else if (userPlayCount < userPlayCount1) {
                    return 1;
                }
                return 1;
            }
        });
        try {
            Object object = json.getJSONObject("tracks").get("track");
            if (object instanceof JSONArray) {
                JSONArray tracks = (JSONArray) object;
                for (int i = 0; i < tracks.length(); i++) {
                    try {
                        JSONObject track = tracks.getJSONObject(i);
                        JSONObject artistJson = track.getJSONObject("artist");
                        String artist = artistJson.optString("name");
                        int playcount = track.optInt("playcount");
                        if (artist.length() == 0) artist = artistJson.optString("#text");
                        Track trackToAdd = new Track(artist, track.optString("name"));
                        trackToAdd.setUserPlayCount(playcount);
                        trackList.add(trackToAdd);
                    } catch (JSONException ignored) {
                        ignored.printStackTrace();
                    }
                }
            } else {
                try {
                    JSONObject track = (JSONObject) object;
                    JSONObject artistJson = track.getJSONObject("artist");
                    String artist = artistJson.optString("name");
                    if (artist.length() == 0) artist = artistJson.optString("#text");
                    trackList.add(new Track(artist, track.optString("name")));
                } catch (JSONException ignored) {
                    ignored.printStackTrace();
                }
            }

        } catch (Exception ignored) {
        }
        List<Track> finalTrackList = new ArrayList<Track>(trackList);
        return new ReadyResult(method, finalTrackList);
    }

    public ReadyResult parseUserGetRecent() {
        List<Track> trackList = new ArrayList<Track>(1);
        try {
            Object object = json.getJSONObject("recenttracks").get("track");
            trackList = getTracksArray(object);
        } catch (Exception ignored) {
        }
        return new ReadyResult(method, trackList);
    }

    public ReadyResult parseGetTopTracksChart() {
        try {
            Object object = json.getJSONObject("tracks").get("track");
            List<Track> trackList = getTracksArray(object);
            return new ReadyResult(method, trackList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseUserTopTracks() {
        List<Track> trackList = new ArrayList<Track>(1);
        try {
            Object object = json.getJSONObject("toptracks").get("track");
            trackList = getTracksArray(object);
        } catch (Exception ignored) {
        }

        return new ReadyResult(method, trackList);
    }

    public ReadyResult parseGetTopTags() {
        try {
            List<Tag> tagList = new ArrayList<Tag>();

            JSONArray tags = json.getJSONObject("tags").getJSONArray("tag");
            for (int i = 0; i < tags.length(); i++) {
                try {
                    JSONObject tag = tags.getJSONObject(i);
                    tagList.add(new Tag(tag.optString("name")));
                    tagList.get(i).setReach(tag.optInt("reach"));
                    tagList.get(i).setTaggings(tag.optLong("taggings"));

                } catch (JSONException ignored) {
                }
            }
            return new ReadyResult(method, tagList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseGetTopArtists(String mainTag) {
        try {
            List<Artist> artistList = new ArrayList<Artist>();
            JSONArray artists = json.getJSONObject(mainTag).getJSONArray("artist");
            for (int i = 0; i < artists.length(); i++) {
                try {
                    JSONObject artistJson = artists.getJSONObject(i);
                    Artist artist = new Artist(artistJson.optString("name"));
                    artist.setListeners(artistJson.optInt("listeners"));
                    artist.setPlaycount(artistJson.optLong("playcount"));
                    artist.setPreviewUrl(artistJson.getJSONArray("image").getJSONObject(3).optString("#text"));
                    artistList.add(artist);
                } catch (JSONException ignored) {
                }
            }
            return new ReadyResult(method, artistList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseGetWeeklyTrackChart() {
        List<Track> trackList = new ArrayList<Track>(1);
        try {
            Object object = json.getJSONObject("weeklytrackchart").get("track");
            trackList = getTracksArray(object);
        } catch (Exception ignored) {
        }
        return new ReadyResult(method, trackList);
    }

    public ReadyResult parseGetHypedArtists() {
        try {
            List<Artist> artistList = new ArrayList<Artist>();

            JSONArray artists = json.getJSONObject("artists").getJSONArray("artist");

            for (int i = 0; i < artists.length(); i++) {
                try {
                    JSONObject artist = artists.getJSONObject(i);
                    artistList.add(new Artist(artist.optString("name"), artist
                            .optInt("percentagechange")));
                    artistList.get(i).setPreviewUrl(
                            artist.getJSONArray("image").getJSONObject(3)
                                    .optString("#text")
                    );

                } catch (JSONException ignored) {
                }
            }
            return new ReadyResult(method, artistList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseGetHypedTracks() {
        try {
            Object object = json.getJSONObject("tracks").get("track");
            List<Track> trackList = getTracksArray(object);
            return new ReadyResult(method, trackList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseGetLastfmPlaylists() {
        try {
            List<Track> trackList = new ArrayList<Track>();
            JSONArray tracks = json.getJSONObject("playlists").optJSONArray("playlist");
            if (tracks != null) {
                for (int i = 0; i < tracks.length(); i++) {
                    try {
                        JSONObject object = tracks.getJSONObject(i);
                        trackList.add(new Track(object.getJSONObject("artist")
                                .optString("name"), object.optString("name")));

                    } catch (JSONException ignored) {
                    }
                }
            }
            return new ReadyResult(method, trackList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseGetArtistTags() {
        try {
            List<Tag> tagList = new ArrayList<Tag>();
            JSONArray tags = json.getJSONObject("toptags").getJSONArray("tag");
            for (int i = 0; i < tags.length(); i++) {
                try {
                    JSONObject tag = tags.getJSONObject(i);
                    tagList.add(new Tag(tag.optString("name")));
                } catch (JSONException ignored) {
                }
            }
            return new ReadyResult(method, tagList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseGetArtistInfo() {
        String imageUrl;
        String biography;
        try {
            JSONObject response = json.getJSONObject("artist");
            JSONArray image = response.getJSONArray("image");
            imageUrl = setMaxImageUrl(image);
            biography = response.getJSONObject("bio").getString("content");
            return new ReadyResult(method, Arrays.asList(imageUrl, biography));
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseGetSimilarArtists() {
        try {
            List<Artist> artistList = new ArrayList<Artist>();
            JSONArray artists = json.getJSONObject("similarartists").getJSONArray("artist");
            for (int i = 0; i < artists.length(); i++) {
                try {
                    JSONObject artist = artists.getJSONObject(i);
                    artistList.add(new Artist(artist.optString("name")));
                    if (!artist.isNull("image")) {
                        artistList.get(i).setPreviewUrl(artist.getJSONArray("image")
                                .getJSONObject(2).optString("#text"));
                    }
                } catch (JSONException ignored) {
                }
            }
            return new ReadyResult(method, artistList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseSearchArtist() {
        try {
            List<Artist> artistList = new ArrayList<Artist>();

            JSONArray artists = json
                    .getJSONObject("results")
                    .getJSONObject("artistmatches").getJSONArray("artist");

            for (int i = 0; i < artists.length(); i++) {
                try {
                    JSONObject artist = artists.getJSONObject(i);
                    artistList.add(new Artist(artist.optString("name")));
                    artistList.get(i).setPreviewUrl(
                            artist.getJSONArray("image").getJSONObject(2)
                                    .optString("#text")
                    );
                } catch (JSONException ignored) {
                }
            }

            return new ReadyResult(method, artistList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseSearchTag() {
        try {
            List<Tag> tagList = new ArrayList<Tag>();

            JSONArray tags = json.getJSONObject("results").getJSONObject("tagmatches")
                    .getJSONArray("tag");

            for (int i = 0; i < tags.length(); i++) {
                try {
                    JSONObject tag = tags.getJSONObject(i);
                    tagList.add(new Tag(tag.optString("name")));

                } catch (JSONException ignored) {
                }
            }
            return new ReadyResult(method, tagList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseSearchAlbum() {
        try {
            List<Album> albumList = new ArrayList<Album>();

            JSONArray albums = json.getJSONObject("results").getJSONObject("albummatches")
                    .getJSONArray("album");
            for (int i = 0; i < albums.length(); i++) {
                try {
                    JSONObject album = albums.getJSONObject(i);
                    String name = album.optString("name");
                    String artist = album.optString("artist");
                    Album albumObject = new Album(artist, name);
                    if (!album.isNull("image")) {
                        albumObject.setImageUrl(album.getJSONArray("image")
                                .getJSONObject(2).optString("#text"));
                    }
                    albumList.add(albumObject);

                } catch (JSONException ignored) {
                }
            }
            return new ReadyResult(method, albumList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseGetArtistsAlbums() {
        try {
            List<Album> albumList = new ArrayList<Album>();
            JSONArray albums = json
                    .getJSONObject("topalbums")
                    .optJSONArray("album");
            if (albums != null) {
                for (int i = 0; i < albums.length(); i++) {
                    try {
                        JSONObject album = albums.getJSONObject(i);
                        String name = album.optString("name");
                        String artist = album.getJSONObject("artist").optString("name");
                        Album albumObject = new Album(artist, name);
                        if (!album.isNull("image")) {
                            albumObject.setImageUrl(album.getJSONArray("image")
                                    .getJSONObject(2).optString("#text"));
                        }
                        albumList.add(albumObject);
                    } catch (JSONException ignored) {
                    }
                }
            }
            return new ReadyResult(method, albumList);
        } catch (JSONException e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseAudioGet() {
        try {
            List<Track> tracks = new ArrayList<Track>();
            JSONArray response = json.optJSONArray("response");
            if (response == null && json.optJSONObject("error") == null) {
                return new ReadyResult(method, tracks);
            }
            for (int i = 0; i < response.length(); i++) {
                Object responseElement;
                try {
                    responseElement = response.get(i);
                    if (JSONObject.class == responseElement.getClass()) {
                        Track track = new Track();
                        JSONObject trackJson = (JSONObject) responseElement;
                        track.setAid(trackJson.optLong("aid"));
                        track.setOwnerId(trackJson.optLong("owner_id"));
                        track.setArtist(trackJson.optString("artist"));
                        track.setTitle(trackJson.optString("title"));
                        track.setUrl(trackJson.optString("url"));
                        tracks.add(track);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return new ReadyResult(method, tracks);
        } catch (Exception e) {
            return parseErrorVk();
        }
    }

    public ReadyResult parseGetVkUserWallAudio() {
        try {
            List<Track> tracks = new ArrayList<Track>();
            JSONArray response = json.optJSONArray("response");
            if (response == null && json.optJSONObject("error") == null) {
                return new ReadyResult(method, tracks);
            }
            for (int i = 0; i < response.length(); i++) {
                Object responseElement;
                try {
                    responseElement = response.get(i);
                    if (JSONObject.class == responseElement.getClass()) {
                        JSONObject post = (JSONObject) responseElement;
                        if (!post.isNull("attachments")) {
                            JSONArray attachments = post.getJSONArray("attachments");
                            for (int j = 0; j < attachments.length(); j++) {
                                JSONObject attachment = (JSONObject) attachments.get(j);
                                Track track = new Track();
                                if ("audio".equals(attachment.get("type"))) {
                                    JSONObject audio = attachment.getJSONObject("audio");
                                    track.setAid(audio.optLong("aid"));
                                    track.setOwnerId(audio.optLong("owner_id"));
                                    track.setArtist(audio.optString("performer"));
                                    track.setTitle(audio.optString("title"));
                                } else {
                                    continue;
                                }
                                if (!tracks.contains(track)) tracks.add(track);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return new ReadyResult(method, new ArrayList<Track>(tracks));
        } catch (Exception e) {
            return parseErrorVk();
        }
    }

    public ReadyResult parseNewsFeed() {
        try {
            List<Track> tracks = new ArrayList<Track>();
            JSONObject response = json.optJSONObject("response");
            if (response == null && json.optJSONObject("error") == null) {
                return new ReadyResult(method, tracks);
            }
            JSONArray items = response.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                Object responseElement;
                try {
                    responseElement = items.get(i);
                    if (JSONObject.class == responseElement.getClass()) {
                        JSONObject post = (JSONObject) responseElement;
                        if (!post.isNull("attachments")) {
                            JSONArray attachments = post.getJSONArray("attachments");
                            for (int j = 0; j < attachments.length(); j++) {
                                JSONObject attachment = (JSONObject) attachments.get(j);
                                Track track = new Track();
                                if ("audio".equals(attachment.get("type"))) {
                                    JSONObject audio = attachment.getJSONObject("audio");
                                    track.setAid(audio.optLong("aid"));
                                    track.setOwnerId(audio.optLong("owner_id"));
                                    track.setArtist(audio.optString("performer"));
                                    track.setTitle(audio.optString("title"));
                                    if (!tracks.contains(track)) tracks.add(track);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return new ReadyResult(method, new ArrayList<Track>(tracks));
        } catch (Exception e) {
            return parseErrorVk();
        }
    }

    public ReadyResult parseVkUserFavoritesAudio() {
        return parseGetVkUserWallAudio();
    }

    public ReadyResult parseSearchVk() {
        try {
            List<Track> tracks = new ArrayList<Track>();
            JSONArray response = json.optJSONArray("response");
            if (response == null && json.optJSONObject("error") == null) {
                return new ReadyResult(method, tracks);
            }
            try {
                int found = (Integer) response.get(0);
                if (found != 0) {
                    for (int i = 1; i < response.length(); i++) {
                        Object responseElement;
                        responseElement = response.get(i);
                        if (JSONObject.class == responseElement.getClass()) {
                            Track track = new Track();
                            JSONObject trackJson = (JSONObject) responseElement;
                            track.setAid(trackJson.optLong("aid"));
                            track.setOwnerId(trackJson.optLong("owner_id"));
                            track.setArtist(trackJson.optString("artist"));
                            track.setTitle(trackJson.optString("title"));
                            track.setUrl(trackJson.optString("url"));
                            tracks.add(track);
                        }
                    }
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            return new ReadyResult(method, tracks);
        } catch (Exception e) {
            return parseErrorVk();
        }
    }

    public ReadyResult parseGetUsers() {
        try {
            List<User> users = new ArrayList<User>();
            JSONArray response = json.optJSONArray("response");
            if (response == null && json.optJSONObject("error") == null) {
                return new ReadyResult(method, users);
            }
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject responseElement = (JSONObject) response.get(i);
                    User user = new User(responseElement.optString("first_name") + " "
                            + responseElement.optString("last_name"));
                    user.setUid(responseElement.optString("uid"));
                    user.setImageUrl(responseElement.optString("photo_medium"));
                    users.add(user);
                } catch (JSONException ignored) {
                }
            }
            return new ReadyResult(method, users);
        } catch (Exception e) {
            return parseErrorVk();
        }
    }

    public ReadyResult parseGetVkGroups() {
        try {
            List<Group> groups = new ArrayList<Group>();
            JSONArray response = json.optJSONArray("response");
            if (response == null && json.optJSONObject("error") == null) {
                return new ReadyResult(method, groups);
            }
            for (int i = 1; i < response.length(); i++) {
                try {
                    Group group = new Group();
                    JSONObject responseElement = (JSONObject) response.get(i);
                    group.setGid(responseElement.optString("gid"));
                    group.setName(responseElement.optString("name"));
                    group.setImageUrl(responseElement.optString("photo_medium"));
                    groups.add(group);
                } catch (JSONException ignored) {
                }
            }
            return new ReadyResult(method, groups);
        } catch (Exception e) {
            return parseErrorVk();
        }
    }

    public ReadyResult parseGetVkAlbums() {
        try {
            List<Album> albums = new ArrayList<Album>();
            try {
                JSONArray response = json.optJSONArray("response");
                if (response == null && json.optJSONObject("error") == null) {
                    return new ReadyResult(method, albums);
                }
                for (int i = 1; i < response.length(); i++) {
                    JSONObject responseElement = (JSONObject) response.get(i);
                    String title = responseElement.optString("title");
                    long albumId = responseElement.optLong("album_id");
                    Album album = new Album(null, title, albumId);
                    albums.add(album);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new ReadyResult(method, albums);
        } catch (Exception e) {
            return parseErrorVk();
        }
    }

    public ReadyResult parseGetVkRecommendations() {
        return parseAudioGet();
    }

    public ReadyResult parseGetVkFriends() {
        return parseGetUsers();
    }

    public ReadyResult parseGetTrackUrl() {
        try {
            JSONArray response = json.optJSONArray("response");
            if (response == null && json.optJSONObject("error") == null) {
                return new ReadyResult(method, null);
            }
            boolean current = false;
            String subMethod = "url";
            try {
                Object res1 = response.opt(0);
                Object res2 = response.opt(1);
                if (res2 != null) {
                    JSONObject responseElement1 = (JSONObject) res2;
                    long aid = responseElement1.optLong("aid");
                    long ownerId = responseElement1.optLong("owner_id");
                    String url = responseElement1.optString("url");
                    return new ReadyResult(subMethod, Arrays.asList(url, aid, ownerId, current));
                } else if (res1 != null) {
                    JSONObject responseElement2 = (JSONObject) res1;
                    long aid = responseElement2.optLong("aid");
                    long ownerId = responseElement2.optLong("owner_id");
                    String url = responseElement2.optString("url");
                    return new ReadyResult(subMethod, Arrays.asList(url, aid, ownerId, current));
                } else {
                    return new ReadyResult(subMethod, null);
                }
            } catch (ClassCastException e2) {
                return new ReadyResult(subMethod, null);
            }
        } catch (Exception e) {
            return parseErrorVk();
        }
    }

    public ReadyResult parseGetTrackLyrics() {
        try {
            JSONArray response = json.optJSONArray("response");
            String subMethod = "lyrics";
            if (response == null && json.optJSONObject("error") == null) {
                return new ReadyResult(subMethod, null);
            }
            try {
                Object res1 = response.opt(0);
                Object res2 = response.opt(1);
                if (res2 != null) {
                    JSONObject responseElement2 = (JSONObject) res2;
                    return new ReadyResult(subMethod, responseElement2.optString("text"));
                } else if (res1 != null) {
                    JSONObject responseElement1 = (JSONObject) res1;
                    return new ReadyResult(subMethod, responseElement1.optString("text"));
                } else {
                    return new ReadyResult(subMethod, null);
                }
            } catch (ClassCastException e) {
                return new ReadyResult(subMethod, null);
            }
        } catch (Exception e) {
            return parseErrorVk();
        }
    }

    public ReadyResult parseGetLovedTracks() {
        List<Track> trackList = new ArrayList<Track>(1);
        int totalPages = Integer.MAX_VALUE;
        try {
            Object object = json.getJSONObject("lovedtracks").get("track");
            trackList = getTracksArray(object);
            totalPages = json.getJSONObject("lovedtracks").getJSONObject("@attr")
                    .getInt("totalPages");
        } catch (Exception ignored) {
        }
        return new ReadyResult(method, trackList, totalPages);
    }

    public ReadyResult parseGetLovedTracksChart() {
        try {
            Object object = json.getJSONObject("tracks").get("track");
            List<Track> trackList = getTracksArray(object);
            return new ReadyResult(method, trackList);
        } catch (Exception e) {
            return parseErrorLastfm();
        }
    }

    public ReadyResult parseSetlists() {
        List<Setlist> setlists = new ArrayList<Setlist>(20);
        try {
            JSONObject response = json.optJSONObject("setlists");
            if (response == null) return new ReadyResult(QueryManager.SETLISTS, setlists);
            JSONArray array = response.optJSONArray("setlist");
            if (response.optInt("@total") == 0)
                return new ReadyResult(QueryManager.SETLISTS, setlists);
            for (int i = 0; i < array.length(); i++) {
                JSONObject setlist = array.optJSONObject(i);
                String artist = setlist.optJSONObject("artist").optString("@name");
                String city = setlist.optJSONObject("venue").optJSONObject("city")
                        .optString("@name");
                String country = setlist.optJSONObject("venue").optJSONObject("city")
                        .optJSONObject("country").optString("@code");
                String venue = setlist.optJSONObject("venue").optString("@name");
                String date = setlist.optString("@eventDate");
                JSONObject setsObject = setlist.optJSONObject("sets");
                List<String> tracks = new ArrayList<String>();
                if (setsObject == null) continue;
                JSONArray sets = setsObject.optJSONArray("set");
                if (sets == null) {
                    try {
                        JSONObject setObject = setsObject.optJSONObject("set");
                        JSONArray songs = setObject.optJSONArray("song");
                        if (songs == null) continue;
                        for (int k = 0; k < songs.length(); k++) {
                            JSONObject song = songs.optJSONObject(k);
                            String songName;
                            songName = song.optString("@name");
                            if (songName.length() == 0) continue;
                            tracks.add(songName);
                        }
                    } catch (NullPointerException ignored) {
                    }
                } else {
                    for (int j = 0; j < sets.length(); j++) {
                        try {
                            JSONObject set = (JSONObject) sets.get(j);
                            JSONArray songs = set.optJSONArray("song");
                            if (songs == null) continue;
                            for (int k = 0; k < songs.length(); k++) {
                                JSONObject song = songs.optJSONObject(k);
                                String songName;
                                songName = song.optString("@name");
                                if (songName.length() == 0) continue;
                                tracks.add(songName);
                            }
                        } catch (JSONException ignored) {
                        } catch (NullPointerException ignored) {
                        }
                    }
                }
                Setlist setlistResult = new Setlist(artist, tracks, city, country, venue, date);
                setlists.add(setlistResult);
            }
            return new ReadyResult(method, setlists);
        } catch (Exception e) {
            return new ReadyResult(method, setlists);
        }
    }

    private ReadyResult parseUploadPhoto() {
        try {
            String server = json.optString("server");
            String photo = json.optString("photo");
            String hash = json.optString("hash");
            return new ReadyResult(method, Arrays.asList(server, photo, hash));
        } catch (Exception e) {
            return new ReadyResult(method, null);
        }
    }

    private ReadyResult parseSaveWallPhoto() {
        try {
            JSONArray values = json.optJSONArray("response");
            JSONObject jsonObject = (JSONObject) values.opt(0);
            String id = jsonObject.optString("id");
            return new ReadyResult(method, id);
        } catch (Exception e) {
            return new ReadyResult(method, null);
        }
    }

    private ReadyResult parseGetPhotosWallUploadServer() {
        try {
            JSONObject response = json.optJSONObject("response");
            return new ReadyResult(method, response.optString("upload_url"));
        } catch (Exception e) {
            return new ReadyResult(method, null);
        }
    }

    private ReadyResult parseGetArtistImages() {
        ArrayList<String> imagesUrls = new ArrayList<String>(36);
        String patternString = "http://userserve-ak.last.fm/serve/126s/";
        String patternNeed = "http://userserve-ak.last.fm/serve/500/";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int end = matcher.end();
            String imageId = text.substring(end, text.indexOf("\"", end));
            imagesUrls.add(patternNeed.concat(imageId));
        }
        return new ReadyResult(null, imagesUrls);
    }

    public ReadyResult voidResult() {
        return new ReadyResult(method, null, methodEnum);
    }

    private ReadyResult parseWallPost() {
        return new ReadyResult(method, null, methodEnum);
    }

    public ReadyResult parse() {
        if ((json == null || json.length() <= 0) && ((text == null) || (text.length() <= 0))) {
            return new ReadyResult(method, null, methodEnum);
        }
        switch (methodEnum) {
            case FRIENDS_GET:
                return parseGetVkFriends();
            case GROUPS_GET:
                return parseGetVkGroups();
            case STATUS_SET:
            case EXECUTE_SEARCH_AND_POST_STATUS:
                return voidResult();
            case AUDIO_ADD:
            case FAST_AUDIO_ADD:
                return voidResult();
            case AUDIO_DELETE:
                return voidResult();
            case USERS_GET:
                return parseGetUsers();
            case ALBUM_SEARCH:
                return parseSearchAlbum();
            case TAG_SEARCH:
                return parseSearchTag();
            case ARTIST_SEARCH:
                return parseSearchArtist();
            case AUTH_GET_MOBILE_SESSION:
                return parseGetMobileSession();
            case ARTIST_GET_SIMILAR:
                return parseGetSimilarArtists();
            case ARTIST_GET_INFO:
                return parseGetArtistInfo();
            case TRACK_SCROBBLE:
                return voidResult();
            case AUDIO_GET:
                return parseAudioGet();
            case TRACK_UPDATE_NOW_PLAYING:
                return voidResult();
            case TRACK_UNLOVE:
                return voidResult();
            case TRACK_LOVE:
                return voidResult();
            case USER_GET_WEEKLY_TRACK_CHART:
                return parseGetWeeklyTrackChart();
            case USER_GET_LOVED_TRACKS:
                return parseGetLovedTracks();
            case CHART_GET_HYPED_TRACKS:
                return parseGetHypedTracks();
            case CHART_GET_HYPED_ARTISTS:
                return parseGetHypedArtists();
            case CHART_GET_LOVED_TRACKS:
                return parseGetLovedTracksChart();
            case CHART_GET_TOP_ARTISTS:
                return parseGetTopArtists("artists");
            case CHART_GET_TOP_TAGS:
                return parseGetTopTags();
            case USER_GET_TOP_TRACKS:
                return parseUserTopTracks();
            case CHART_GET_TOP_TRACKS:
                return parseGetTopTracksChart();
            case LIBRARY_GET_TRACKS:
                return parseGetUserLibrary();
            case USER_GET_RECENT_TRACKS:
                return parseUserGetRecent();
            case TRACK_GET_INFO:
                return parseGetTrackInfo();
            case ALBUM_GET_INFO:
                return parseAlbumGetInfo();
            case USER_GET_INFO:
                return parseUserGetInfo();
            case ARTIST_GET_TOP_TRACKS:
                return parseArtistTopTracks();
            case TAG_GET_TOP_TRACKS:
                return parseTagTopTracks();
            case USER_GET_FRIENDS:
                return parseFriendsLastfm();
            case USER_GET_RECOMMENDED_ARTISTS:
                return parseRecommendedArtists();
            case USER_GET_NEIGHBOURS:
                return parseNeighbours();
            case AUDIO_SEARCH:
                return parseSearchVk();
            case EXECUTE_URL:
                return parseGetTrackUrl();
            case EXECUTE_LYRICS:
                return parseGetTrackLyrics();
            case AUDIO_GET_ALBUMS:
                return parseGetVkAlbums();
            case NEWS_FEED_POST:
                return parseNewsFeed();
            case ERROR:
                return voidResult();
            case LIBRARY:
                return parseGetUserLibrary();
            case RADIOMIX:
                return parseGetUserLibrary();
            case SETLISTS:
                return parseSetlists();
            case AUDIO_GET_RECOMMENDATIONS:
                return parseGetVkRecommendations();
            case WALL_GET:
                return parseGetVkUserWallAudio();
            case WALL_POST:
                return parseWallPost();
            case FAVE_GET_POSTS:
                return parseVkUserFavoritesAudio();
            case ARTIST_GET_TAGS:
                return parseGetArtistTags();
            case USER_GET_PLAYLISTS:
                return parseGetLastfmPlaylists();
            case ARTIST_GET_ALBUMS:
                return parseGetArtistsAlbums();
            case USER_GET_TOP_ARTISTS:
                return parseGetTopArtists("topartists");
            case ARTIST_IMAGES:
                return parseGetArtistImages();
            case PHOTOS_GET_WALL_UPLOAD_SERVER:
                return parseGetPhotosWallUploadServer();
            case PHOTOS_SAVE_WALL_PHOTO:
                return parseSaveWallPhoto();
            case UPLOAD_PHOTO:
                return parseUploadPhoto();
            default:
                return voidResult();
        }
    }
}
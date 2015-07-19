package com.pillowapps.liqear.adapters.gson;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbums;

import java.lang.reflect.Type;
import java.util.List;

public class LastfmAlbumMatchesGsonAdapter implements JsonDeserializer<LastfmAlbums> {
    public LastfmAlbums deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
        LastfmAlbums lastfmAlbums = new LastfmAlbums();
        if (json.toString().trim().equals("\"\\n\"")) {
            // Strange lastfm api behaviour
            return lastfmAlbums;
        } else {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement albumsJson = jsonObject.get("album");
            if (albumsJson != null) {
                Type listType = new TypeToken<List<LastfmAlbum>>() {
                }.getType();
                List<LastfmAlbum> albums = new Gson().fromJson(albumsJson, listType);
                lastfmAlbums.setAlbums(albums);
            }
            return lastfmAlbums;
        }
    }
}

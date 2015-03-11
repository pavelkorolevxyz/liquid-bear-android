package com.pillowapps.liqear.adapters.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LastfmTrackListGsonAdapter implements JsonDeserializer<List<LastfmTrack>> {
    public List<LastfmTrack> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
        List<LastfmTrack> vals = new ArrayList<>();
        if (json.isJsonArray()) {
            for (JsonElement e : json.getAsJsonArray()) {
                vals.add((LastfmTrack) ctx.deserialize(e, LastfmTrack.class));
            }
        } else if (json.isJsonObject()) {
            if (json.getAsString().trim().length() == 0) return new ArrayList<>();
            vals.add((LastfmTrack) ctx.deserialize(json, LastfmTrack.class));
        } else {
            throw new RuntimeException("Unexpected JSON type: " + json.getClass());
        }
        return vals;
    }
}

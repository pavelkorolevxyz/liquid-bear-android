package com.pillowapps.liqear.helpers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.pillowapps.liqear.models.lastfm.LastfmTrack;

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
            vals.add((LastfmTrack) ctx.deserialize(json, LastfmTrack.class));
        } else {
            throw new RuntimeException("Unexpected JSON type: " + json.getClass());
        }
        return vals;
    }
}

package com.pillowapps.liqear.adapters.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSet;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmTrack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SetlistfmSongListGsonAdapter implements JsonDeserializer<List<SetlistfmTrack>> {
    public List<SetlistfmTrack> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
        List<SetlistfmTrack> vals = new ArrayList<>();
        if (json.isJsonArray()) {
            for (JsonElement e : json.getAsJsonArray()) {
                vals.add((SetlistfmTrack) ctx.deserialize(e, SetlistfmTrack.class));
            }
        } else if (json.isJsonObject()) {
            vals.add((SetlistfmTrack) ctx.deserialize(json, SetlistfmTrack.class));
        } else {
            throw new RuntimeException("Unexpected JSON type: " + json.getClass());
        }
        return vals;
    }
}

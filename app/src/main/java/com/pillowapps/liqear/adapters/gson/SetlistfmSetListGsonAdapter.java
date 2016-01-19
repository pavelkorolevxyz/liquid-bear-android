package com.pillowapps.liqear.adapters.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSet;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SetlistfmSetListGsonAdapter implements JsonDeserializer<List<SetlistfmSet>> {
    public List<SetlistfmSet> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
        List<SetlistfmSet> vals = new ArrayList<>();
        if (json.isJsonArray()) {
            for (JsonElement e : json.getAsJsonArray()) {
                vals.add(ctx.deserialize(e, SetlistfmSet.class));
            }
        } else if (json.isJsonObject()) {
            vals.add(ctx.deserialize(json, SetlistfmSet.class));
        } else {
            throw new RuntimeException("Unexpected JSON type: " + json.getClass());
        }
        return vals;
    }
}

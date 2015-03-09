package com.pillowapps.liqear.adapters.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSet;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSets;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmTrack;

import java.lang.reflect.Type;
import java.util.List;

public class SetlistfmSetsAdapter implements JsonDeserializer<SetlistfmSets> {
    public SetlistfmSets deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
        Type setListTypeAdapter = new TypeToken<List<SetlistfmSet>>() {
        }.getType();
        Type trackListTypeAdapter = new TypeToken<List<SetlistfmTrack>>() {
        }.getType();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(setListTypeAdapter, new SetlistfmSetListGsonAdapter())
                .registerTypeAdapter(trackListTypeAdapter, new SetlistfmSongListGsonAdapter())
                .create();
        try {
            String sets = json.getAsString();
            if ("".equals(sets)) {
                return null;
            } else {
                return gson.fromJson(sets, SetlistfmSets.class);
            }
        } catch (Exception e) {
            return gson.fromJson(json, SetlistfmSets.class);
        }
    }
}

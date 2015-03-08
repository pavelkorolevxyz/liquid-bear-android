package com.pillowapps.liqear.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public class LastfmBooleanGsonAdapter implements JsonDeserializer<Boolean> {
    public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
        String lastfmBoolString = json.getAsString();
        return "1".equals(lastfmBoolString);
    }
}

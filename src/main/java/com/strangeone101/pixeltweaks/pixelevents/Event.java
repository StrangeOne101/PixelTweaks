package com.strangeone101.pixeltweaks.pixelevents;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.strangeone101.pixeltweaks.music.MusicEvent;

import java.lang.reflect.Type;
import java.util.List;

public abstract class Event {

    public String type;
    public List<Condition> conditions;

    transient String pack;
    transient String file;

    public static class Deserializer implements JsonDeserializer<Event> {

        @Override
        public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String musicType = jsonObject.get("type").getAsString();
            if (jsonObject.has("pack")) {
                //Remove the pack field as it can't be set
                jsonObject.remove("pack");
            }

            if ("bgm".equals(musicType)) {
                return context.deserialize(json, MusicEvent.BGM.class);
            } else if ("battle_music".equals(musicType) || "battlemusic".equals(musicType)) {
                return context.deserialize(json, MusicEvent.Battle.class);
            }

            throw new JsonParseException("Invalid music type: " + musicType);
        }
    }

    public String getPack() {
        return pack;
    }

    public String getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type='" + type + '\'' +
                ", conditions=" + conditions +
                ", pack='" + pack + '\'' +
                '}';
    }
}

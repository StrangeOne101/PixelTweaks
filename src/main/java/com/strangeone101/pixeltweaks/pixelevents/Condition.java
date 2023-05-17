package com.strangeone101.pixeltweaks.pixelevents;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.condition.BiomeCondition;
import com.strangeone101.pixeltweaks.pixelevents.condition.PlayerCondition;
import com.strangeone101.pixeltweaks.pixelevents.condition.PokemonCondition;
import com.strangeone101.pixeltweaks.pixelevents.condition.RangeCondition;
import com.strangeone101.pixeltweaks.pixelevents.condition.StructureCondition;
import com.strangeone101.pixeltweaks.pixelevents.condition.TrainerCondition;

import java.lang.reflect.Type;

public abstract class Condition<T> {

    public String type;

    // Add getters and setters for the 'type' field

    public static class Deserializer implements JsonDeserializer<Condition<?>> {

        @Override
        public Condition<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String musicType = jsonObject.get("type").getAsString();

            if ("pokemon".equals(musicType)) {
                return context.deserialize(json, PokemonCondition.class);
            } else if ("range".equals(musicType)) {
                return context.deserialize(json, RangeCondition.class);
            } else if ("biome".equals(musicType)) {
                return context.deserialize(json, BiomeCondition.class);
            } else if ("structure".equals(musicType)) {
                return context.deserialize(json, StructureCondition.class);
            } else if ("player".equals(musicType)) {
                return context.deserialize(json, PlayerCondition.class);
            } else if ("trainer".equals(musicType)) {
                return context.deserialize(json, TrainerCondition.class);
            }

            throw new JsonParseException("Invalid music type: " + musicType);
        }
    }

    protected abstract boolean conditionMet(T item);

    public abstract T itemFromPixelmon(PixelmonEntity entity);

    public boolean conditionMet(PixelmonEntity entity) {
    	return conditionMet(itemFromPixelmon(entity));
    }

    @Override
    public String toString() {
        return "Condition{" +
                "type='" + type + '\'' +
                '}';
    }
}

package com.strangeone101.pixeltweaks.client.overlay;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import com.strangeone101.pixeltweaks.struct.Fade;
import net.minecraft.util.ResourceLocation;

import java.awt.Color;
import java.lang.reflect.Type;

public class OverlayLayer {

    public double offset = 0;
    public boolean emissive = false;
    public ResourceLocation texture;
    public Color color = Color.WHITE;
    public float alpha = 1F;
    public Fade fade = null;

    public static class Deserializer implements JsonDeserializer<OverlayLayer> {

        @Override
        public OverlayLayer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            OverlayLayer layer = new OverlayLayer();
            if (!json.isJsonObject()) {
                if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
                    layer.texture = new ResourceLocation(json.getAsString());
                    return layer;
                } else {
                    throw new JsonParseException("OverlayLayer must be a string or an object!");
                }
            }
            if (json.getAsJsonObject().has("offset")) layer.offset = json.getAsJsonObject().get("offset").getAsDouble();
            if (json.getAsJsonObject().has("emissive")) layer.emissive = json.getAsJsonObject().get("emissive").getAsBoolean();
            if (json.getAsJsonObject().has("texture")) layer.texture = ResourceLocationHelper.ofTexture(new ResourceLocation(json.getAsJsonObject().get("texture").getAsString()));
            if (json.getAsJsonObject().has("color")) {
                JsonElement element = json.getAsJsonObject().get("color");
                if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                    layer.color = new Color(element.getAsInt());
                } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                    layer.color = Color.decode(element.getAsString());
                } else if (element.isJsonArray()) {
                    layer.color = new Color(element.getAsJsonArray().get(0).getAsInt(), element.getAsJsonArray().get(1).getAsInt(), element.getAsJsonArray().get(2).getAsInt());
                } else if (element.isJsonObject()) {
                    int r = element.getAsJsonObject().has("red") ? element.getAsJsonObject().get("red").getAsInt() : 0;
                    int g = element.getAsJsonObject().has("green") ? element.getAsJsonObject().get("green").getAsInt() : 0;
                    int b = element.getAsJsonObject().has("blue") ? element.getAsJsonObject().get("blue").getAsInt() : 0;
                    layer.color = new Color(r, g, b);
                } else throw new JsonParseException("Invalid color format!");
            }
            if (json.getAsJsonObject().has("alpha")) layer.alpha = json.getAsJsonObject().get("alpha").getAsFloat();
            if (json.getAsJsonObject().has("fade")) layer.fade = context.deserialize(json.getAsJsonObject().get("fade"), Fade.class);

            return layer;
        }
    }
}

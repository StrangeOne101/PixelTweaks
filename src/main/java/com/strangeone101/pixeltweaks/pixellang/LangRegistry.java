package com.strangeone101.pixeltweaks.pixellang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class LangRegistry {

    private static final Map<String, String> DEFAULTS = new HashMap<>();
    private static final Map<String, JsonObject> REGISTRY = new HashMap<>();

    private static final String DEFAULT_LANG = "en_us";

    public LangRegistry() {
        DEFAULTS.put("en", "en_us");

        MinecraftForge.EVENT_BUS.addListener(this::registerReload);
    }

    protected static String resolveLocale(String locale) {
        if (REGISTRY.containsKey(locale)) return locale;

        String baseLang = DEFAULTS.get(locale.substring(0, 2)); //Get the first 2 letters
        if (DEFAULTS.containsKey(baseLang)) return DEFAULTS.get(baseLang);

        return DEFAULT_LANG;
    }

    public static void sendMessage(ServerPlayer player, String langNode, Object... args) {
        player.sendSystemMessage(getMessage(player, langNode, args));
    }

    public static Component getMessage(ServerPlayer player, String langNode, Object... args) {
        String locale = player.getLanguage();
        String resolved = resolveLocale(locale);
        JsonObject object = REGISTRY.get(resolved);

        if (object == null) {
            return Component.translatable(langNode, args);
        }

        String message = String.format(object.get(langNode).getAsString(), args);
        if (message == null) {
            return Component.translatable(langNode, args);
        }

        return Component.literal(message);
    }


    protected void registerReload(AddReloadListenerEvent event) {
        event.addListener(new LangReloader());
    }

    private static class LangReloader extends SimpleJsonResourceReloadListener {

        public LangReloader() {
            super(new GsonBuilder().setPrettyPrinting().setLenient().create(), "pixellang");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            for (ResourceLocation resourceLocation : resourceLocationJsonElementMap.keySet()) {
                String code = resourceLocation.getPath();

                JsonObject object = resourceLocationJsonElementMap.get(resourceLocation).getAsJsonObject();
                if (REGISTRY.containsKey(code)) {
                    JsonObject oldObject = REGISTRY.get(code);

                    for (Map.Entry<String, JsonElement> entry : oldObject.entrySet()) {
                        if (!object.has(entry.getKey())) {
                            object.add(entry.getKey(), entry.getValue());
                        }
                    }
                }

                REGISTRY.put(code, object);
            }
        }

        @Override
        public String getName() {
            return "PixelTweaks Lang Reloader";
        }
    }


}

package com.strangeone101.pixeltweaks.pixellang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

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

    public static void sendMessage(ServerPlayerEntity player, String langNode, Object... args) {
        player.sendMessage(getMessage(player, langNode, args), null);
    }

    public static ITextComponent getMessage(ServerPlayerEntity player, String langNode, Object... args) {
        String locale = player.getLanguage();
        String resolved = resolveLocale(locale);
        JsonObject object = REGISTRY.get(resolved);

        if (object == null) {
            return new TranslationTextComponent(langNode, args);
        }

        String message = String.format(object.get(langNode).getAsString(), args);
        if (message == null) {
            return new TranslationTextComponent(langNode, args);
        }

        return new StringTextComponent(message);
    }


    protected void registerReload(AddReloadListenerEvent event) {
        event.addListener(new LangReloader());
    }

    private static class LangReloader extends JsonReloadListener {

        public LangReloader() {
            super(new GsonBuilder().setPrettyPrinting().setLenient().create(), "pixellang");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
            for (ResourceLocation resourceLocation : objectIn.keySet()) {
                String code = resourceLocation.getPath();

                JsonObject object = objectIn.get(resourceLocation).getAsJsonObject();
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
        public String getSimpleName() {
            return "PixelTweaks Lang Reloader";
        }
    }


}

package com.strangeone101.pixeltweaks.pixelevents;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.client.overlay.OverlayLayer;
import com.strangeone101.pixeltweaks.music.Sound;
import com.strangeone101.pixeltweaks.struct.SpecificTime;
import net.minecraft.client.Minecraft;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

public class EventRegistry implements PreparableReloadListener {

    private static Map<Class<? extends Event>, Set<Event>> EVENTS = Maps.newHashMap();

    protected static void registerEvent(Event event) {
        EVENTS.computeIfAbsent(event.getClass(), k -> newTreeset()).add(event);

        if (event instanceof EventListener) {
            ((EventListener) event).onRegister();
        }
    }

    public static TreeSet<Event> newTreeset() {
        return Sets.newTreeSet(
        (e1, e2) -> {
            if (e1.getPriority() == e2.getPriority()) return e2.hashCode() - e1.hashCode();
            return e2.getPriority() - e1.getPriority();
        });
    }

    public static <T extends Event> Collection<T> getEvents(Class<T> eventClass) {
        return (Collection<T>) EVENTS.getOrDefault(eventClass, Sets.newHashSet());
    }

    public EventRegistry() {
        //MinecraftForge.EVENT_BUS.addListener(this::onResourcesReloadEvent);
        if (Minecraft.getInstance().getResourceManager() instanceof ReloadableResourceManager) {
            PixelTweaks.LOGGER.debug("Added music loader");
            ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this);
        } else {
            PixelTweaks.LOGGER.debug("Failed to add music loader");
        }

    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller1, Executor executor, Executor executor1) {
        String directoryPath = "pixelevents";

        PixelTweaks.LOGGER.debug("Loading events from " + directoryPath);

        // Get files from resource packs. We use a set to stop double ups from occurring
        Map<ResourceLocation, Resource> fileLocations = resourceManager.listResources(directoryPath, file -> file.toDebugFileName().toLowerCase().endsWith(".json"));

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Event.class, new Event.Deserializer())
                .registerTypeAdapter(Condition.class, new Condition.Deserializer())
                .registerTypeAdapter(ResourceLocation.class, (JsonDeserializer<ResourceLocation>)(json, type, context) -> json.isJsonNull() || json.getAsString().isEmpty() ? null : new ResourceLocation(json.getAsString()))
                .registerTypeAdapter(PokemonSpecification.class, (JsonDeserializer<PokemonSpecification>)(json, type, context) -> PokemonSpecificationProxy.create(json.getAsString()).get())
                .registerTypeAdapter(SpecificTime.class, new SpecificTime.Deserializer())
                .registerTypeAdapter(OverlayLayer.class, new OverlayLayer.Deserializer())
                .registerTypeAdapter(Sound.class, new Sound.Deserializer())
                .create();

        int loadedSuccessfully = 0;
        for (ResourceLocation location : fileLocations.keySet()) {
            PixelTweaks.LOGGER.debug(location.toString());
            try {
                Resource resource = fileLocations.get(location);
                    InputStream stream = resource.open();
                    Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

                    Event event = gson.fromJson(reader, Event.class);
                    event.pack = resource.sourcePackId();
                    event.file = location.getPath();
                    PixelTweaks.LOGGER.debug(event);
                    registerEvent(event);
                    loadedSuccessfully++;
                } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        PixelTweaks.LOGGER.info("Loaded " + loadedSuccessfully + " event files");

        return CompletableFuture.completedFuture(null);
    }
}

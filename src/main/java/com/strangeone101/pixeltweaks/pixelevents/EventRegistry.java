package com.strangeone101.pixeltweaks.pixelevents;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.strangeone101.pixeltweaks.PixelTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class EventRegistry implements ISelectiveResourceReloadListener {

    private static Map<Class<? extends Event>, Set<Event>> EVENTS = Maps.newHashMap();

    protected static void registerEvent(Event event) {
        EVENTS.computeIfAbsent(event.getClass(), k -> Sets.newTreeSet(Comparator.comparingInt(Event::getPriority).reversed())).add(event);
    }

    public static <T extends Event> Collection<T> getEvents(Class<T> eventClass) {
        return (Collection<T>) EVENTS.getOrDefault(eventClass, Sets.newHashSet());
    }

    public EventRegistry() {
        //MinecraftForge.EVENT_BUS.addListener(this::onResourcesReloadEvent);
        if (Minecraft.getInstance().getResourceManager() instanceof IReloadableResourceManager) {
            PixelTweaks.LOGGER.debug("Added music loader");
            ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(this);
        } else {
            PixelTweaks.LOGGER.debug("Failed to add music loader");
        }

    }

    /*public void onResourcesReloadEvent(ClientLi event) {
        event.addListener(this);
        PixelTweaks.LOGGER.info("Added music loader");
    }*/

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        String directoryPath = "pixelevents";

        PixelTweaks.LOGGER.debug("Loading music from " + directoryPath);

        // Get files from resource packs. We use a set to stop double ups from occurring
        Set<ResourceLocation> fileLocations = Sets.newHashSet(resourceManager.getAllResourceLocations(directoryPath, file -> file.toLowerCase().endsWith(".json")));

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Event.class, new Event.Deserializer())
                .registerTypeAdapter(Condition.class, new Condition.Deserializer())
                .registerTypeAdapter(ResourceLocation.class, (JsonDeserializer<ResourceLocation>)(json, type, context) -> json.isJsonNull() ? null : new ResourceLocation(json.getAsString()))
                .registerTypeAdapter(PokemonSpecification.class, (JsonDeserializer<PokemonSpecification>)(json, type, context) -> PokemonSpecificationProxy.create(json.getAsString()))
                .create();

        for (ResourceLocation location : fileLocations) {
            PixelTweaks.LOGGER.debug(location.toString());
            try {
                IResource resource = resourceManager.getResource(location);
                InputStream stream = resource.getInputStream();
                Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));


                Event event = gson.fromJson(reader, Event.class);
                event.pack = resource.getPackName();
                event.file = location.getPath();
                PixelTweaks.LOGGER.debug(event);
                registerEvent(event);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        PixelTweaks.LOGGER.info("Loaded " + fileLocations.size() + " event files");

        // Get files from mods
        /*try {
            Path modsDir = FMLPaths.MODSDIR.get();
            Files.walk(modsDir)
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        String relativePath = modsDir.relativize(filePath).toString();
                        fileLocations.add(new ResourceLocation("mod", relativePath));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    /**
     * A version of onResourceManager that selectively chooses {@link IResourceType}s
     * to reload.
     * When using this, the given predicate should be called to ensure the relevant resources should
     * be reloaded at this time.
     *
     * @param resourceManager   the resource manager being reloaded
     * @param resourcePredicate predicate to test whether any given resource type should be reloaded
     */
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {

    }
}
